# 第2章 Java内存区域与内存溢出异常

## 运行时数据区域

> Java虚拟机在执行Java程序的过程中会把它所管理的内存划分为若干个不同的数据区域。这些区域都有各自的用途，以及创建和销毁的时间，有的区域随着虚拟机进程的启动而存在，有些区域则依赖用户线程的启动和结束而建立和销毁。

Java 虚拟机的内存模型分为两部分：一部分是线程共享的，包括 Java `堆`和`方法区`；另一部分是线程私有的，包括`虚拟机栈`和`本地方法栈`，以及`程序计数器`这一小部分内存。

**以下为Java SE 6 的内存模型：**

![](..\Resources\JVM(Java SE6).png)

Java SE7中，存储在永久代的部分数据就已经转移到了Java Heap或者是Native Memory。但永久代仍存在于JDK1.7中，并没完全移除，譬如符号引用(Symbols)转移到了Native Memory；Interned Strings转移到了java heap；类的静态变量(Class Statics)转移到了Java Heap。

在Java SE8 Hotspot中，永久代(Permanent Generation)已被彻底删除,以Mataspace VM(存在与本地内存)代替。

> 元空间与永久代之间最大的区别在于：元空间并不在虚拟机中，而是使用本地内存

> 为了方便区别受GC管理的内存与不受GC管理的内存，才专门把GC heap单独拿出来说，而把GC heap以外的内存叫native memory。——[RednaxelaFX](https://www.zhihu.com/people/rednaxelafx)

> PermGen是在GC heap内的，但是不在Java heap内。HotSpot VM以前的GC heap = Java heap + PermGen。——[RednaxelaFX](https://www.zhihu.com/people/rednaxelafx)

### 程序计数器（Program Counter Register）

1. 如果线程正在执行的是Java 方法，则这个计数器记录的是正在执行的虚拟机字节码指令地址
2. 如果正在执行的是Native 方法，则这个计数器值为空（Undefined）
3. 此内存区域是唯一一个在Java虚拟机规范中没有规定任何OutOfMemoryError(No OOM)情况的区域
4. 线程隔离性，每个线程工作时都有属于自己的独立计数器。

### 虚拟机栈（Java Virtual Machine Stacks）

> 生命周期与线程相同。
>
> 虚拟机栈描述的是Java方法执行的内存模型：每个方法在执行的同时都会创建一个栈帧(**Stack Frame**; 是方法运行时的基础数据结构) 用于存储局部变量表（**Array of local variables**）、操作数栈（**Operand stack**）、动态链接（**Reference to runtime constant pool (Dynamic linking)**）、方法出口(**Return Value**)等信息。

局部变量表存放了编译期可知的各种基本类型&对象引用(reference类型，它不等同于对象本身，可能是一个指向对象初始地址的引用指针，也可能指向一个代表对象的句柄或其他的此对象相关的位置)和ReturnAddress 类型(指向了一条字节码指令的地址)。

其中64位长度的long & double类型的数据会占用2个局部变量空间（Slot）,其余类型只占用1个。

可能发生OOM&SOF（StackOverFlowError）异常。

> slot 是局部变量表中的空间单位，虚拟机规范中有规定，对于 32 位之内的数据，用一个 slot 来存放，如 int，short，float 等；对于 64 位的数据用连续的两个 slot 来存放，如 long，double 等。引用类型的变量 JVM 并没有规定其长度，它可能是 32 位，也有可能是 64 位的，所以既有可能占一个 slot，也有可能占两个 slot。

### 本地方法栈（Native Method Stack）

本地方法栈和Java虚拟机栈类似**，**区别在于Java虚拟机栈是为了Java方法服务的，而**本地方法栈是为了native方法服务的**。在虚拟机规范中并没有对本地方法实现所采用的编程语言与数据结构采取强制规定，因此不同的JVM虚拟机可以自己实现自己的native方法。此处需要说明：Sun HotSpot虚拟机就直接将本地方法栈和Java虚拟机栈合二为一了。

和Java虚拟机一样可能发生OOM&SOF异常。

### Java堆（Java Heap）

Java堆是Java虚拟机所管理的内存中最大的一块。

Java堆是被所有线程共享的一块内存区域，在JVM启动时创建。

此内存区域的唯一目的就是存放对象实例，几乎所有的对象实例都在这里分配内存。

Java堆是垃圾收集器管理的主要区域，因此很多时候也被称做“GC堆”（Garbage Collected Heap）。

可能发生OOM异常。

### 方法区（Method Area）

方法区与Java堆一样，是各个线程共享的内存区域，它被用于存储已被JVM加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。虽然Java虚拟机规范把方法区描述为堆的一个逻辑部分，但是它却有一个别名为Non-Heap，与Java Heap 相区分。

在`JDK8`之前方法区存在于永久代（**permanent generation**），在JDK8的HotSpot VM它被元数据区（`Metaspace`）代替。两者并没有本质的区别，只是PermGen直接受GC管理，而Metaspace则是在Native Memory里分配并且间接受GC管理而已。

一般来说PermGen == method area。

>   例如，Method area要持有“代码”（一般操作系统进程里至少主程序的代码段就在text段里），而PermGen里的对象所持有的“代码”只有Java字节码，而没包含JIT编译器后的代码；**HotSpot VM里所有动态生成的代码都存在另一个空间里，CodeCache**。同理，JVM要管理symbol（CONSTANT_Utf8对应物）和interned string，对应有SymbolTable和StringTable，这俩也是在native memory而不在PermGen里的。这些东西结合起来都应该算是method area的一部分。
>
>   反过来说，JDK6和之前的HotSpot VM的PermGen里确实也存有一些对象不属于method area要处理的范围。这些就是不来自Class文件的CONSTANT_String的interned string。
>   I**nterned string有两种，一种来自Class文件的常量池（CONSTANT_String），这些理应属于method area；另一种来自用户代码调用String.intern()**（虽然best practice是不要在用户代码里调用这个方法），这些只应该算是普通的Java对象而不应该属于method area。
>
>   JVM规范说：
>   Although the method area is logically part of the heap, simple implementations may choose not to either garbage collect or compact it.
>   “方法区逻辑上是堆的一部分”。只看这句的话，方法区是Java heap的一部分。
>   但HotSpot VM把PermGen、CodeCache等都标记为“non-heap”，而只有young gen+old gen是Java heap。  
>
>   Sun JDK6的HotSpot VM在PermGen里存的对象，按类型划分，有：
>
>   非Java对象：
>   instanceKlass -> Java类/接口的元数据
>   typeArrayKlass -> 原始类型数组的类型的元数据
>   objArrayKlass -> 引用类型数组的类型的元数据
>   *KlassKlass -> 描述*Klass对象的元数据
>   constantPool -> 运行时常量池，每个类有一个对应的
>   method -> 方法的元数据（主要存可变的部分），每个方法有一份
>   constMethod -> 方法元数据中不变的部分；每个方法有一份。字节码就嵌在这个对象末尾
>   symbol -> CONSTANT_Utf8对应的常量字符串。UTF-8编码。不是java.lang.String实例。Oracle JDK7挪到了native memory。
>   methodData -> 简称MDO。存有方法的profile数据，每个方法有一份
>
>   上述对象都是根正苗红的属于方法区的对象。
>   除*KlassKlass和symbol外，它们在Oracle JDK8都被挪到了Metaspace里。
>   *KlassKlass在JDK8去掉了，彻底没了。
>   symbol在JDK7开始就挪到了native memory，通过引用计数来管理。
>
>   Java对象：
>   java.lang.String及其背后的char[] -> CONSTANT_String对应的interned string，以及String.intern()对应的interned string。JDK7挪到了普通Java heap。
>   还有少量short[]、int[]之类的来存储元数据。这些虽然是Java对象但对Java应用不可见。这些在Oracle JDK8挪到了Java heap或者不再用Java对象（改用native的GrowableArray之类）。
>
>   这些Java对象有些可以算是属于方法区里，有些不应该算在方法区里；但它们确实都是Java对象所以必然都应该在JVM规范所说的Java heap里。  
>
>   ​																	——[RednaxelaFX](https://www.zhihu.com/people/rednaxelafx)

可能发生OOM异常。

#### 运行时常量池（Runtime Constant Pool）

运行时常量池是方法区的一部分。Class文件中除了有类的版本、字段、方法、接口等描述信息外，还有一项信息是常量池（Constant Pool Table），用于存放编译期生成的各种字面量**（Literal）**和符号引用**(Symbolic References)**，这部分内容将在类加载后进入方法区的运行时常量池。

> While it is correct that every class has a [constant pool in its class file](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4), it might be misleading to say that every class will have a runtime constant pool (on its own). That’s again a JVM implementation detail. While it is possible to map each class constant pool 1:1 to a runtime constant pool, it obviously makes sense to merge the constant pools of classes living in the same resolve context (i.e. defined by the same class loader) into one pool, so that identical constants don’t need to be resolved multiple times. Though, conceptionally, every class has a runtime representation of its pool, even if they do not materialize in this naive form. So the statement “every class has a runtime constant pool” is not wrong, but it doesn’t necessarily imply that there will be such a data structure for every class.
>
> ​																——[Holger](https://stackoverflow.com/users/2711488/holger)

### 字符串常量池（String Literal Pool）

全局字符串池里的内容是在类加载完成，经过验证，准备阶段之后在堆中生成字符串对象实例，然后将该字符串对象实例的引用值存到string pool中（**String pool中存的是引用值而不是具体的实例对象，具体的实例对象是在堆中开辟的一块空间存放的。**）。 在HotSpot VM里实现的string pool功能的是一个StringTable类，它是一个哈希表，里面存的是key（字面量“abc”, 即驻留字符串）-value（字符串"abc"实例对象在堆中的引用）键值对，也就是说在堆中的某些字符串实例被这个StringTable引用之后就等同被赋予了”驻留字符串”的身份。这个StringTable在每个HotSpot VM的实例只有一份，被所有的类共享（享元模式）

#### String.intern()

> 在jdk1.6中 intern 方法会把首次遇到的字符串实例复制到永久代（PermGen）中，并返回此引用；但在jdk1.7中，只是会把首次遇到的字符串实例的引用添加到常量池(Java Heap区域)，并返回此引用。

------

参考：

[知乎 | RednaxelaFX - HotSpot Java虚拟机中的“方法区”“持久代”“元数据区”的关系？](https://www.zhihu.com/question/27429881)

[Stackoverfolw | Holger - Runtime constant pool - is filled up by variables created in runtime?](https://stackoverflow.com/questions/43047852/runtime-constant-pool-is-filled-up-by-variables-created-in-runtime)

[Stackoverfolw | JVM architecture: Runtime constant pool in Method area is per-class](https://stackoverflow.com/questions/40640566/jvm-architecture-runtime-constant-pool-in-method-area-is-per-class)