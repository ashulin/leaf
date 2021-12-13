# Java 并发容器

## CAS

CAS（比较与交换，Compare and swap） 是一种有名的**无锁算法**

- CAS是一个**原子操作**

CAS有**3个**操作数

- **内存值V**
- **旧的预期值A**
- **要修改的新值B**

> 当多个线程尝试使用CAS同时更新同一个变量时，只有其中一个线程能更新变量的值(**A和内存值V相同时，将内存值V修改为B)**，而其它线程都失败，失败的线程**并不会被挂起**，而是被告知这次竞争中失败，并可以再次尝试**(否则什么都不做)**

## volatile

Java编程语言允许线程访问共享变量， 为了确保共享变量能被准确和一致地更新，线程应该确保通过排他锁单独获得这个变量。Java语言提供了volatile，是最轻量级的同步机制，在某些情况下比锁要更加方便。

volatile在多处理器开发中保证了共享变量的`可见性`。

当定义一个变量为volatile时，它就具备了三层语义：

- 可见性（Visibility）：在多线程环境下，一个变量的写操作总是对其后的读取线程可见 
- 原子性（Atomicity）：volatile的读/写操作具有原子性
- 有序性（Ordering）：禁止指令的重排序优化，JVM会通过插入内存屏障（Memory Barrier）指令来保证

## ConcurrentHashMap

>以下基于*JDK 1.8*源码；

### 数据结构

1.8中放弃了`Segment`臃肿的设计，取而代之的是采用`Node` + `CAS` + `Synchronized`来保证并发安全进行实现，采用和`JDK 1.8`的*HashMap*同一个结构，具体结构如下：

![ConcurrentHashMap-Data Structure](..\Resources\ConcurrentHashMap-Data Structure.png)

### 属性

```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V>
    implements ConcurrentMap<K,V>, Serializable {
    // 默认初始化容量
	private static final int DEFAULT_CAPACITY = 16;
	private static final int MAXIMUM_CAPACITY = 1 << 30;
	// 最大容量
	static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	// 默认并发等级，为了兼容旧版本的segment；
	// 唯一的作用就是保证构造map时初始容量不小于concurrencyLevel
	private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
	// 加载因子
	private static final float LOAD_FACTOR = 0.75f;
	// 链表树化最小节点数
	static final int TREEIFY_THRESHOLD = 8;
	// 红黑树链表化节点数
	static final int UNTREEIFY_THRESHOLD = 6;
	// 最小树化容量
	static final int MIN_TREEIFY_CAPACITY = 64;
	// 扩容线程每次最少要迁移16个hash桶
	// min_transfer_stride 在扩容中，参与的单个线程允许处理的最少table桶首节点个数
	// 虽然适当添加线程，会使得整个扩容过程变快，但需要考虑多线程内存同时分配的问题
	private static final int MIN_TRANSFER_STRIDE = 16;
	// resize stamp bits sizeCtl 中记录 size 的 bit 数
	private static int RESIZE_STAMP_BITS = 16;
	// max_resizers 2^15-1 参与扩容的最大线程数
	private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;
	// 32 - 16 = 16, sizeCtl 中记录 size 大小的偏移量
	private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

	// 节点的hash值为-1; 这是一个ForwardingNode节点
	static final int MOVED     = -1; // hash for forwarding nodes
	// 节点的hash值为-2; 这是一个TreeBin节点
    static final int TREEBIN   = -2; // hash for roots of trees
    // ReservationNode 的 hash 值
    static final int RESERVED  = -3; // hash for transient reservations
    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash
    // 可用处理器数量
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    /** For serialization compatibility. */
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("segments", Segment[].class),
        new ObjectStreamField("segmentMask", Integer.TYPE),
        new ObjectStreamField("segmentShift", Integer.TYPE)
    };
    // hash桶，它的容量一定为2的的整数次幂
	transient volatile Node<K,V>[] table;
    // 扩容时候使用,平时为null，只有在扩容的时候才为非null
    private transient volatile Node<K,V>[] nextTable;
    // 基础计数器，主要在没有线程竞争时使用；
    private transient volatile long baseCount;
    /** 
	 * hash表初始化或扩容时的一个控制位标识量;
     *  根据变量的数值不同，hash表处于不同阶段
     *  1. = -1 : 正在初始化
     *  2. < -1 : 正在扩容，数值为 -(1 + 参与扩容的线程数)
     *  3. = 0  : 创建时初始为0
     *  4. > 0  : 下一次扩容的大小
	 */
	private transient volatile int sizeCtl; 
    private transient volatile int transferIndex;
    // 与计算size有关
    private transient volatile int cellsBusy;
    private transient volatile CounterCell[] counterCells;
    
    private transient KeySetView<K,V> keySet;
    private transient ValuesView<K,V> values;
    private transient EntrySetView<K,V> entrySet;
}
```

### 构造器

```java
    public ConcurrentHashMap() {
    }
    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }
    public ConcurrentHashMap(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException();
        // 对initialCapacity 做处理后再获取 接近2的正数次幂的值；
        int cap = ((initialCapacity >= (MAXIMUM_CAPACITY >>> 1)) ?
                   MAXIMUM_CAPACITY :
                   tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1));
        this.sizeCtl = cap;
    }
    public ConcurrentHashMap(int initialCapacity,
                             float loadFactor, int concurrencyLevel) {
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();
        if (initialCapacity < concurrencyLevel)   // Use at least as many bins
            initialCapacity = concurrencyLevel;   // as estimated threads
        // 对initialCapacity 做处理后再获取 接近2的正数次幂的值；可以看出我们传入的加载因子只在初始化时有用
        long size = (long)(1.0 + (long)initialCapacity / loadFactor);
        int cap = (size >= (long)MAXIMUM_CAPACITY) ?
            MAXIMUM_CAPACITY : tableSizeFor((int)size);
        this.sizeCtl = cap;
    }
    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
        this.sizeCtl = DEFAULT_CAPACITY;
        putAll(m);
    }
	// 用来保证hash桶数量一定为2的n次方
    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```

### 重要类

#### Node

- 对value和next属性设置了volatile同步锁

- 不允许调用setValue方法直接改变Node的value

- 增加了find方法辅助map.get()方法

```java
	// 单向链表
	static class Node<K,V> implements Map.Entry<K,V> {
        // key和val不允许为null;
        final int hash;// Node节点的hash值和key的hash值相同
        final K key;
        volatile V val;// 带有同步锁的value(保证可见性)  
        volatile Node<K,V> next;// 带有同步锁的next指针

        Node(int hash, K key, V val, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        public final K getKey()       { return key; }
        public final V getValue()     { return val; }
        public final int hashCode()   { return key.hashCode() ^ val.hashCode(); }
        public final String toString(){ return key + "=" + val; }
        // 不允许直接改变value的值，HashMap允许
        public final V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        public final boolean equals(Object o) {
            Object k, v, u; Map.Entry<?,?> e;
            return ((o instanceof Map.Entry) &&
                    (k = (e = (Map.Entry<?,?>)o).getKey()) != null &&
                    (v = e.getValue()) != null &&
                    (k == key || k.equals(key)) &&
                    (v == (u = val) || v.equals(u)));
        }

        /**
         * Virtualized support for map.get(); overridden in subclasses.
         * 通过hash & key取出链表下对应节点
         */
        Node<K,V> find(int h, Object k) {
            Node<K,V> e = this;
            if (k != null) {
                do {
                    K ek;
                    if (e.hash == h &&
                        ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                } while ((e = e.next) != null);
            }
            return null;
        }
    }
```

#### TreeNode

```java
    /** 
     *	红黑树节点信息
	 * 只有红黑树的基本属性，没有红黑树对应的方法，红黑树性质由TreeBin完成;
	 * TreeNode只承担包装数据的作用;
	 */ 
	static final class TreeNode<K,V> extends Node<K,V> {
        TreeNode<K,V> parent;  // red-black tree links
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;

        TreeNode(int hash, K key, V val, Node<K,V> next,
                 TreeNode<K,V> parent) {
            super(hash, key, val, next);
            this.parent = parent;
        }

        Node<K,V> find(int h, Object k) {
            return findTreeNode(h, k, null);
        }

        /**
         * Returns the TreeNode (or null if not found) for the given key
         * starting at given root.
         * 通过hash值大小，确定查询方向；
         * 如果hash相等，key不等，通过key的比较器比较：
         * 如果没有比较器，先遍历右子树再遍历左子树；
         */
        final TreeNode<K,V> findTreeNode(int h, Object k, Class<?> kc) {
            if (k != null) {
                TreeNode<K,V> p = this;
                do  {
                    int ph, dir; K pk; TreeNode<K,V> q;
                    TreeNode<K,V> pl = p.left, pr = p.right;
                    if ((ph = p.hash) > h)
                        p = pl;
                    else if (ph < h)
                        p = pr;
                    else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
                        return p;
                    else if (pl == null)
                        p = pr;
                    else if (pr == null)
                        p = pl;
                    else if ((kc != null ||
                              (kc = comparableClassFor(k)) != null) &&
                             (dir = compareComparables(kc, k, pk)) != 0)
                        p = (dir < 0) ? pl : pr;
                    else if ((q = pr.findTreeNode(h, k, kc)) != null)
                        return q;
                    else
                        p = pl;
                } while (p != null);
            }
            return null;
        }
    }
```

#### TreeBin

- 插入时，不加锁，直到插入完成需要重新平衡时，获取CAS平时

```java
// 用于封装TreeNode，也就是说，ConcurrentHashMap的红黑树存放的是TreeBin，而不是treeNode。  
	static final class TreeBin<K,V> extends Node<K,V> {
        // 红黑树根节点
        TreeNode<K,V> root;
        volatile TreeNode<K,V> first;
        volatile Thread waiter;
        volatile int lockState;
        // values for lockState
        // 持有写锁
        static final int WRITER = 1; // set while holding write lock
        // 等待写锁
        static final int WAITER = 2; // set when waiting for write lock
        // 读锁的递增值
        static final int READER = 4; // increment value for setting read lock
        
        /**
         * Creates bin with initial set of nodes headed by b.
         */
        TreeBin(TreeNode<K,V> b) {
            super(TREEBIN, null, null, null);// TREEBIN常量表明这是一个红黑树节点；
            this.first = b;
            TreeNode<K,V> r = null;
            // 遍历TreeNode的链表信息，转换为红黑树
            for (TreeNode<K,V> x = b, next; x != null; x = next) {
                next = (TreeNode<K,V>)x.next;
                x.left = x.right = null;
                if (r == null) {// 设为头节点
                    x.parent = null;
                    x.red = false;
                    r = x;
                }
                else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    // 遍历红黑树插入节点，并平衡性质；
                    for (TreeNode<K,V> p = r;;) {
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((kc == null &&
                                  (kc = comparableClassFor(k)) == null) ||
                                 (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);
                        TreeNode<K,V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            r = balanceInsertion(r, x);
                            break;
                        }
                    }
                }
            }
            this.root = r;
            assert checkInvariants(root);
        }
        // 省略了红黑树的左右旋、移除/新增节点、移除/新增节点后的平衡方法等
        
        // 从root开始递归检查红黑树的性质，仅在检查root是否落在table上时调用
        static <K,V> boolean checkInvariants(TreeNode<K,V> t) {
            TreeNode<K,V> tp = t.parent, tl = t.left, tr = t.right,
                tb = t.prev, tn = (TreeNode<K,V>)t.next;
            if (tb != null && tb.next != t)
                return false;
            if (tn != null && tn.prev != t)
                return false;
            if (tp != null && t != tp.left && t != tp.right)
                return false;
            if (tl != null && (tl.parent != t || tl.hash > t.hash))
                return false;
            if (tr != null && (tr.parent != t || tr.hash < t.hash))
                return false;
            if (t.red && tl != null && tl.red && tr != null && tr.red)
                return false;
            if (tl != null && !checkInvariants(tl))
                return false;
            if (tr != null && !checkInvariants(tr))
                return false;
            return true;
        }
        // 树重组时的写锁
        private final void lockRoot() {
            if (!U.compareAndSwapInt(this, LOCKSTATE, 0, WRITER))
                contendedLock(); // offload to separate method
        }
// 因为ConcurrentHashMap的写方法会给头节点加锁，所以读写锁不用考虑写写竞争的情况，只用考虑读写竞争的情况
        private final void contendedLock() {
            boolean waiting = false;
            for (int s;;) {
                // lockState == WAITER || lockState == 0
                if (((s = lockState) & ~WAITER) == 0) {// 没有线程持有读锁时尝试获取写锁
                    if (U.compareAndSwapInt(this, LOCKSTATE, s, WRITER)) {
                        if (waiting)// 拿到锁后将等待线程清空（等待线程是它自己）
                            waiter = null;
                        return;
                    }
                }
                // 有线程持有读锁且本线程状态不为WAITER时
                else if ((s & WAITER) == 0) {// lockState != WAITER
                    // 尝试占有waiting线程
                    if (U.compareAndSwapInt(this, LOCKSTATE, s, s | WAITER)) {
                        waiting = true;
                        waiter = Thread.currentThread();
                    }
                }
                // 有线程持有读锁且本线程状态为WAITER时，堵塞自己
                else if (waiting)
                    LockSupport.park(this);// 挂起线程
            }
        }
        // 释放写锁
        private final void unlockRoot() {
            lockState = 0;
        }
        //重写find方法，当写锁被持有时使用链表查询的方法
        final Node<K,V> find(int h, Object k) {
            if (k != null) {
                for (Node<K,V> e = first; e != null; ) {
                    int s; K ek;
                    // 写锁被持有时使用链表的方法遍历
                    if (((s = lockState) & (WAITER|WRITER)) != 0) {
                        if (e.hash == h &&
                            ((ek = e.key) == k || (ek != null && k.equals(ek))))
                            return e;
                        e = e.next;
                    }
                    // CAS的方式递增一个读锁值
                    // 写锁未被持有，获取一个读锁；
                    else if (U.compareAndSwapInt(this, LOCKSTATE, s,s + READER)) {
                        TreeNode<K,V> r, p;
                        try {
                            p = ((r = root) == null ? null :
                                 r.findTreeNode(h, k, null));
                        } finally {
                            Thread w;
                            // 获取旧锁值，并递减一个读锁值
                            if (U.getAndAddInt(this, LOCKSTATE, -READER) ==
                                (READER|WAITER) && (w = waiter) != null)// 更新后等于等待写锁
                                LockSupport.unpark(w);
                            // 当前线程持有最后一个读锁的时候通知waiter线程获取写锁
                            // unpark释放许可；
                            // park/unpark能够精准的对线程进行唤醒和等待;
                            // park在执行过程中首选看是否有许可，有许可就立马返回;
                        }
                        return p;
                    }
                }
            }
            return null;
        }
    }
```

#### ForwardingNode

- 在扩容时才会出现的特殊节点，只有hash为MOVED表明当前在扩容。并拥有nextTable指针引用新的table数组。

```java
// 并不是我们传统的包含key-value的节点，只是一个标志节点，并且指向nextTable，提供find方法而已。
// 生命周期：仅存活于扩容操作且bin不为null时，一定会出现在每个bin的首位。  
	static final class ForwardingNode<K,V> extends Node<K,V> {
        final Node<K,V>[] nextTable;
        ForwardingNode(Node<K,V>[] tab) {
            super(MOVED, null, null, null);
            this.nextTable = tab;
        }

        Node<K,V> find(int h, Object k) {
            // 查nextTable节点，outer避免深度递归  
            // loop to avoid arbitrarily deep recursion on forwarding nodes
            outer: for (Node<K,V>[] tab = nextTable;;) {
                Node<K,V> e; int n;
                if (k == null || tab == null || (n = tab.length) == 0 ||
                    (e = tabAt(tab, (n - 1) & h)) == null)
                    return null;
                for (;;) {
                    int eh; K ek;
                    if ((eh = e.hash) == h &&
                        ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                    if (eh < 0) {
                        if (e instanceof ForwardingNode) {
                            tab = ((ForwardingNode<K,V>)e).nextTable;
                            continue outer;
                        }
                        else
                            return e.find(h, k);
                    }
                    if ((e = e.next) == null)
                        return null;
                }
            }
        }
    }
```

#### Traverser

```java
    static class Traverser<K,V> {
        Node<K,V>[] tab;        // current table; updated if resized
        Node<K,V> next;         // the next entry to use; 下一个要访问的entry
        TableStack<K,V> stack, spare; // to save/restore on ForwardingNodes; 发现forwardingNode时，保存/修复当前tab相关信息
        int index;              // index of bin to use next; 下一个要访问的hash桶索引
        int baseIndex;          // current index of initial table; 当前正在访问的初始tab的hash桶索引
        int baseLimit;          // index bound for initial table; 初始tab的hash桶索引边界
        final int baseSize;     // initial table size; 初始tab的长度

        Traverser(Node<K,V>[] tab, int size, int index, int limit) {
            this.tab = tab;
            this.baseSize = size;
            this.baseIndex = this.index = index;
            this.baseLimit = limit;
            this.next = null;
        }

        /**
         * Advances if possible, returning next valid node, or null if none.
         *  如果有可能，返回下一个有效节点，否则返回null。
         */
        final Node<K,V> advance() {
            Node<K,V> e;
            if ((e = next) != null)
                e = e.next;
            for (;;) {
                Node<K,V>[] t; int i, n;  // must use locals in checks
                if (e != null)
                    return next = e;// e为空，说明此链表已经遍历完成，准备遍历下一个hash桶
                if (baseIndex >= baseLimit || (t = tab) == null ||
                    (n = t.length) <= (i = index) || i < 0)
                    return next = null;// 到达边界，返回null
                if ((e = tabAt(t, i)) != null && e.hash < 0) {// 获取下一个hash桶对应的node链表的头节点
                    if (e instanceof ForwardingNode) {// 转发节点,说明此hash桶中的节点已经迁移到了nextTable
                        tab = ((ForwardingNode<K,V>)e).nextTable;
                        e = null;
                        pushState(t, i, n);
                        continue;
                    }
                    else if (e instanceof TreeBin)
                        e = ((TreeBin<K,V>)e).first;
                    else
                        e = null;
                }
                if (stack != null)
                    // 此时遍历的是迁移目标nextTable,尝试回退到源table，
                	// 继续遍历源table中的节点
                    recoverState(n);
                else if ((index = i + baseSize) >= n)
                    // 初始tab的hash桶索引+1 ，即遍历下一个hash桶
                    index = ++baseIndex; // visit upper slots if present
            }
        }

        /**
         * Saves traversal state upon encountering a forwarding node.
         * 在遇到转发节点时以压栈的方式保存遍历状态
         */
        
        private void pushState(Node<K,V>[] t, int i, int n) {
            TableStack<K,V> s = spare;  // reuse if possible
            if (s != null)
                spare = s.next;
            else
                s = new TableStack<K,V>();
            s.tab = t;
            s.length = n;
            s.index = i;
            s.next = stack;
            stack = s;
        }

        /**
         * Possibly pops traversal state.
         * 可能会弹出遍历状态
         * @param n length of current table
         */
        private void recoverState(int n) {
            TableStack<K,V> s; int len;
             // (s = stack) != null :stack不空，说明此时遍历的是nextTable
      		//  (index += (len = s.length)) >= n: 确保了按照index,
      		// index+tab.length的顺序遍历nextTable,条件成立表示nextTable已经遍历完毕
            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                // 弹出tab，获取tab的遍历状态，开始遍历tab中的桶
                n = len;
                index = s.index;
                tab = s.tab;
                s.tab = null;
                TableStack<K,V> next = s.next;
                s.next = spare; // save for reuse
                stack = next;
                spare = s;
            }
            if (s == null && (index += baseSize) >= n)
                index = ++baseIndex;
        }
    }
```

#### Unsafe

```java
    // Unsafe mechanics
	// 对应属性名在对象中的内存位置
    private static final sun.misc.Unsafe U;
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final long ABASE;
    private static final int ASHIFT;

    static {
        try {
            U = sun.misc.Unsafe.getUnsafe();
            Class<?> k = ConcurrentHashMap.class;
            SIZECTL = U.objectFieldOffset
                (k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = U.objectFieldOffset
                (k.getDeclaredField("transferIndex"));
            BASECOUNT = U.objectFieldOffset
                (k.getDeclaredField("baseCount"));
            CELLSBUSY = U.objectFieldOffset
                (k.getDeclaredField("cellsBusy"));
            Class<?> ck = CounterCell.class;
            CELLVALUE = U.objectFieldOffset
                (ck.getDeclaredField("value"));
            Class<?> ak = Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
	// 以 volatile 读的方式读取 table 数组中的元素
    static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
        return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }
	// 以CAS的方式更新一个元素
	// 原子的执行如下逻辑：如果tab[i]==c,则设置tab[i]=v，并返回ture.否则返回false
    static final <K,V> boolean casTabAt(Node<K,V>[] tab, int i,
                                        Node<K,V> c, Node<K,V> v) {
        return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
    }
	// 以 valatile 写的方式，将元素插入 table 数组
    static final <K,V> void setTabAt(Node<K,V>[] tab, int i, Node<K,V> v) {
        U.putObjectVolatile(tab, ((long)i << ASHIFT) + ABASE, v);
    }
```

### 扰动函数

```java
	static final int HASH_BITS = 0x7fffffff; 
	static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }
```

### put

1. *key & vaule* 不允许为*null*，获取扰动后的hash值；

2. 判断`hash桶`是否初始化，未初始化调用*initTable*初始化；

3. 以*index = (n - 1) & hash*获取头节点；

    - 头节点为空，以***CAS***方式插入节点；

    - 头节点为`ForwardingNode`，表明正在扩容，调用*helpTransfer*协助扩容；

    - 头节点存在且未在扩容，使用***synchronized***锁住头节点；

        - 如果当前为链表***Node***: 遍历链表，判断是覆盖旧值还是插入新值，并记录链表长度；

        - 如果当前为红黑树***TreeBin***: 以红黑树方式*put*键值对，如果是插入新值，先插入，然后竞争写锁，重新平衡红黑树，释放锁；

    - 释放***synchronized***锁，判断链表长度是否超过树化阀值，如果超过，调用*treeifyBin*判断是因为扩容还是树化；如果存在旧值（即是覆盖操作），返回旧值；
    
4. 如果是插入，调用*addCount*重新统计键值对数量；

```java
    public V put(K key, V value) {
        return putVal(key, value, false);
    }
 	public V putIfAbsent(K key, V value) {
        return putVal(key, value, true);
    }
    /** Implementation for put and putIfAbsent */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        // key & vaule 不能为null;
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {// 获取当前table，进入死循环,直到插入成功！
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)// 如果table未初始化
                tab = initTable();// 初始化
            // 寻址方式仍然为 index = (n - 1) & hash
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))// 以CAS的方式插入节点
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);// 协助扩容
            else {
                V oldVal = null;
                synchronized (f) {// 获取桶首节点实例对象锁，进入临界区进行添加操作
                    // 双检索：再次判断f是否仍然符合条件
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {// 桶首节点hash值>0，表示为链表
                            binCount = 1;// 记录链表长度
                            // 遍历链表插入或替换旧值
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {// 红黑树
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                // 判断是否应该扩容或转为红黑树
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);// 没有达到最小树化容量则扩容，否则树化；
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        // 更新计算count时的base和counterCells数组
        addCount(1L, binCount);
        return null;
    }
```

### initTable

- 获取***sizeCtl***值保存，然后判断***sizeCtl***是否小于0
    - 小于0，即当前有线程在初始化，调用*Thread.yield()*让出线程；
    - 大于等于0，使用***CAS***将***sizeCtl***设置为 -1 锁住；
        - 双检锁再判断是否未初始化，如果原***sizeCtl***为0（即未设置初始化容量），使用默认容量16；
        - 初始化hash桶；
        - 重新计算扩容阀值；
        - 释放锁退出；

```java
    private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0)// sizeCtl = -1 表示正在初始化
                 // 已经有其他线程在执行初始化，则主动让出cpu
            	// 1. 保证只有一个线程正在进行初始化操作
                Thread.yield(); // lost initialization race; just spin
         	// 利用CAS操作设置sizeCtl为-1
       	 	// 设置成功表示当前线程为执行初始化的唯一线程
        	// 此处进入临界区
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    // 由于让出cpu的线程也会后续进入该临界区
                	// 需要进行再次确认table是否为null
                    if ((tab = table) == null || tab.length == 0) {
                        // 2. 得出数组的大小
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        // 3. 这里才真正的初始化数组，即分配Node数组
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        // 4. 计算数组中可用的大小：实际大小n*0.75（加载因子）
                        sc = n - (n >>> 2);
                    }
                } finally {
                    sizeCtl = sc;// 释放锁
                }
                break;
            }
        }
        return tab;
    }
```

### transfer

- 通过*NCPU*判断每次迁移的*Node*个数`stride`，如果小于16，再设置为16；

- 判断`nextTab`临时桶是否存在，不存在则新建hash桶，容量为旧桶的两倍，并赋予`nextTab`；

- 以`nextTab`初始化一个***ForwardingNode***；

- 逆序更新`index`；

    - 判断`index`是否仍在边界`bound`内，或已完成迁移`finishing`；
    - 判断下一个迁移位置`transferIndex`<=0，即是否已经没有需要迁移的hash桶；
    - 根据`transferIndex`和`stride`，更新边界`bound`和当前迁移位置`index`；

- 以*i < 0 || i >= n || i + n >= nextn*判断是否已迁移完成

    - i<0，说明已经遍历完旧的hash桶

    - i >= n，这个代码块内有一处将，`index`设置为n，

    - 以***CAS***方式将***sizeCtl***设置为***sizeCtl + 1***

        - 因为调用*transfer*方法的如*tryPresize/addCount/helpTransfer*方法，协助扩容线程都会将***sizeCtl***设置为***sizeCtl - 1***

        - 如果*sc == (resizeStamp(n) << RESIZE_STAMP_SHIFT) + 2*；即为最后一个结束的线程

            > 因为如*tryPresize/addCount*方法第一个扩容的线程都会设置将***sizeCtl***设置为*(resizeStamp(n) << RESIZE_STAMP_SHIFT) + 2)*

        - 最后一个结束的后续将新桶`nextTab`赋予hash桶`table`，并更新新桶为null，更新扩容阀值`sizeCtl`为新容量的0.75倍；

- 如果当前节点为空，不需要复制，将该节点以CAS方式赋予***ForwardingNode***；

- 如果当前节点为***ForwardingNode***，即已被处理，跳过

- 如果不为空，且也未被处理，则开始使用***synchronized***锁住头节点，开始迁移

  - 双检锁再判断节点是否已被改动；
  - 如果当前为链表；以*hash & n == 0*的方式将链表处理成两个反序排列的链表（有一部分没被反序），*hash & n == 0*的为低位节点，不等的高位节点；
  - 如果当前为红黑树；以和红黑树同样的方式处理成两个***TreeNode***，处理过程中记录两个红黑树的节点数
      - 如果节点数小于等于链表化阀值`UNTREEIFY_THRESHOLD`，调用*untreeify*方法链表化
      - 否则调用*new TreeBin(TreeNode n)*构建为红黑树
  - 将低位节点放置在新桶`index`，高位节点放置到`index + n`
  - 将当前节点赋予***ForwardingNode***，处理完成；

```java
    // 负责迁移node节点
	private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
        int n = tab.length, stride;
        // 计算每次迁移的node个数（MIN_TRANSFER_STRIDE该值作为下限，以避免扩容线程过多）
        if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
            // 确保每次迁移的node个数不少于16个
            stride = MIN_TRANSFER_STRIDE; // subdivide range
        // nextTab为扩容中的临时table
        if (nextTab == null) {            // initiating
            try {
                @SuppressWarnings("unchecked")
                // 1. 新建一个 node 数组，容量为之前的两倍
                Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
                nextTab = nt;
            } catch (Throwable ex) {      // try to cope with OOME
                sizeCtl = Integer.MAX_VALUE;
                return;
            }
            nextTable = nextTab;// 迁移过程中先将扩容后的新桶给予 extTable
            // transferIndex为扩容复制过程中的桶首节点遍历索引
            // 所以从n开始，表示从后向前遍历
            transferIndex = n;
        }
        int nextn = nextTab.length;
  		//2. 新建forwardingNode引用，在之后会用到
        ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
        boolean advance = true;
        // 循环的关键变量，判断是否已经扩容完成，完成就 return , 退出循环
        boolean finishing = false; // to ensure sweep before committing nextTab
        //【1】逆序迁移已经获取到的hash桶集合，如果迁移完毕，则更新transferIndex，
        // 获取下一批待迁移的hash桶
        //【2】如果transferIndex=0，表示所以hash桶均被分配，将i置为-1，准备退出transfer方法
        for (int i = 0, bound = 0;;) {
            // 3. 确定遍历中的索引i（更新待迁移的hash桶索引）
          	// 循环的关键 i , i-- 操作保证了倒叙遍历数组
            Node<K,V> f; int fh;
            while (advance) {
                int nextIndex, nextBound;
                // 更新迁移索引i
                if (--i >= bound || finishing)
                    advance = false;
                // transferIndex = 0表示table中所有数组元素都已经有其他线程负责扩容
              	// nextIndex = transferIndex = n = tab.length(默认16)
                else if ((nextIndex = transferIndex) <= 0) {
                    // transferIndex<=0表示已经没有需要迁移的hash桶，
                  	// 将i置为-1，线程准备退出
                    i = -1;
                    advance = false;
                }
                // CAS无锁算法设置 transferIndex = transferIndex - stride		
             	// 尝试更新transferIndex，获取当前线程执行扩容复制的索引区间
             	// 当前线程负责完成索引为(nextBound，nextIndex)之间的桶首节点扩容
            	// 当迁移完bound这个桶后，尝试更新transferIndex，获取下一批待迁移的hash桶
                else if (U.compareAndSwapInt
                         (this, TRANSFERINDEX, nextIndex,
                          nextBound = (nextIndex > stride ?
                                       nextIndex - stride : 0))) {
                    bound = nextBound;
                    i = nextIndex - 1;
                    advance = false;
                }
            }
            //4.将原数组中的元素复制到新数组中去
            // for循环退出，扩容结束修改sizeCtl属性
            if (i < 0 || i >= n || i + n >= nextn) {// 扩容完成
                int sc;
                if (finishing) {
                    // 最后一个迁移的线程，recheck后，做收尾工作，然后退出
                    nextTable = null;
                    table = nextTab;
                    // 设置新sizeCtl，仍然为总大小的0.75
                    sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                // 第一个扩容的线程，执行transfer方法之前，会设置 sizeCtl = 
                // (resizeStamp(n) << RESIZE_STAMP_SHIFT) + 2) 	
                // 后续帮其扩容的线程，执行transfer方法之前，会设置 sizeCtl = sizeCtl+1
                // 每一个退出transfer的方法的线程，退出之前，会设置 sizeCtl = sizeCtl-1
                // 那么最后一个线程退出时：
                // 必然有sc == (resizeStamp(n) << RESIZE_STAMP_SHIFT) + 2)，
                // 即 (sc - 2) == resizeStamp(n) << RESIZE_STAMP_SHIFT
                if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                    if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                        return;
                    finishing = advance = true;
                    i = n; // recheck before commit
                }
            }
            //4.1 当前数组中第i个元素为null，用CAS设置成特殊节点forwardingNode(可以理解成占位符)
            else if ((f = tabAt(tab, i)) == null)
                advance = casTabAt(tab, i, null, fwd);
            // 当前table节点已经是ForwardingNode
            // 表示已经被其他线程处理了，则直接往前遍历
            // 通过CAS读写ForwardingNode节点状态，达到多线程互斥处理
          	// 4.2 如果遍历到ForwardingNode节点说明这个点已经被处理过了直接跳过
            // 这里是控制并发扩容的核心
          	// 如果 f.hash=-1 的话说明该节点为 ForwardingNode,说明该节点已经处理过了
            else if ((fh = f.hash) == MOVED)
                advance = true; // already processed
            else {
                synchronized (f) {// 锁住当前桶首节点
                    if (tabAt(tab, i) == f) {// 双检锁
                        Node<K,V> ln, hn;// 高低位节点
                        if (fh >= 0) {// 当前是链表
                            int runBit = fh & n;// 
                            Node<K,V> lastRun = f;
                            // 获取最后一个截取节点
                            for (Node<K,V> p = f.next; p != null; p = p.next) {
                                int b = p.hash & n;
                                if (b != runBit) {
                                    runBit = b;
                                    lastRun = p;
                                }
                            }
                		   // 根据最后截取节点的runBit值放在对应位置
                            if (runBit == 0) {
                                ln = lastRun;
                                hn = null;
                            }
                            else {
                                hn = lastRun;
                                ln = null;
                            }
                            // 遍历前半部分，反序构建链表
                            for (Node<K,V> p = f; p != lastRun; p = p.next) {
                                int ph = p.hash; K pk = p.key; V pv = p.val;
                                if ((ph & n) == 0)
                                    ln = new Node<K,V>(ph, pk, pv, ln);
                                else
                                    hn = new Node<K,V>(ph, pk, pv, hn);
                            }
                            //以 valatile 写的方式，将元素插入 table 数组
                            setTabAt(nextTab, i, ln);// (ph & n) == 0 在原index位置
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);// 设置旧桶当前位置为ForwordingNode节点
                            advance = true;// 当前桶位置迁移完成
                        }
                        else if (f instanceof TreeBin) {// 当前是红黑树
                            TreeBin<K,V> t = (TreeBin<K,V>)f;
                            // 记录高低位TreeNode头节点和尾节点
                            TreeNode<K,V> lo = null, loTail = null;
                            TreeNode<K,V> hi = null, hiTail = null;
                            int lc = 0, hc = 0;// 记录长度
                            // hash & oldLen 生成高低位两个链表
                            for (Node<K,V> e = t.first; e != null; e = e.next) {
                                int h = e.hash;
                                TreeNode<K,V> p = new TreeNode<K,V>
                                    (h, e.key, e.val, null, null);
                                if ((h & n) == 0) {
                                    if ((p.prev = loTail) == null)
                                        lo = p;
                                    else
                                        loTail.next = p;
                                    loTail = p;
                                    ++lc;
                                }
                                else {
                                    if ((p.prev = hiTail) == null)
                                        hi = p;
                                    else
                                        hiTail.next = p;
                                    hiTail = p;
                                    ++hc;
                                }
                            }
                            // 根据长度判断是否树化或者链表化
                            ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                                (hc != 0) ? new TreeBin<K,V>(lo) : t;
                            hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                                (lc != 0) ? new TreeBin<K,V>(hi) : t;
                            //以 valatile 写的方式，将元素插入 table 数组
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);// 设置旧桶当前位置为ForwordingNode节点
                            advance = true;// 当前桶位置迁移完成
                        }
                    }
                }
            }
        }
    }
```

### helpTransfer

```java
    final Node<K,V>[] helpTransfer(Node<K,V>[] tab, Node<K,V> f) {
        Node<K,V>[] nextTab; int sc;
        // 判断当前节点是否为ForwardingNode节点，且是否已扩容完成
        if (tab != null && (f instanceof ForwardingNode) &&
            (nextTab = ((ForwardingNode<K,V>)f).nextTable) != null) {
            
            int rs = resizeStamp(tab.length);
            while (nextTab == nextTable && table == tab &&
                   (sc = sizeCtl) < 0) {
                if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                    sc == rs + MAX_RESIZERS || transferIndex <= 0)
                    break;
                if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                    transfer(tab, nextTab);
                    break;
                }
            }
            return nextTab;
        }
        return table;
    }
	// 计算一个操作校验码
    static final int resizeStamp(int n) {
        // numberOfLeadingZeros返回二进制数的前导零个数；
        // RESIZE_STAMP_BITS = 16;
        return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
    }
```

### treeifyBin

```java
    private final void treeifyBin(Node<K,V>[] tab, int index) {
        Node<K,V> b; int n, sc;
        if (tab != null) {
            if ((n = tab.length) < MIN_TREEIFY_CAPACITY)// 
                tryPresize(n << 1);// 尝试扩容；
            else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
                synchronized (b) {
                    if (tabAt(tab, index) == b) {//双检锁
                        TreeNode<K,V> hd = null, tl = null;
                        // 将Node包装成TreeNode
                        for (Node<K,V> e = b; e != null; e = e.next) {
                            TreeNode<K,V> p =
                                new TreeNode<K,V>(e.hash, e.key, e.val,
                                                  null, null);
                            if ((p.prev = tl) == null)
                                hd = p;
                            else
                                tl.next = p;
                            tl = p;
                        }
                        // 将TreeNode转为红黑树TreeBin，并插入index桶
                        setTabAt(tab, index, new TreeBin<K,V>(hd));
                    }
                }
            }
        }
    }
```

### tryPresize

> 该方法只有*treeifyBin*和*putAll*调用

- 计算扩容目标容量`cap`

- 取得`sizeCtl`，判断是否已在扩容；

    - 如果当前桶尚未初始化

        - 以***CAS***方式将`SIZECTL`置为 -1，表示正在进行初始化
        - 双检索后，初始化桶，更新桶和计算新的扩容阀值
        - 将扩容阀值赋予`sizeCtl`，即释放锁

    - 如果目标容量`cap`小于扩容阀值，或者容量超过最大限制时，不需要扩容

    - 如果需要扩容

        - 如果已有其他线程在扩容，判断是否扩容完成，未完成则协助扩容，将`SIZECTL`设置为`SIZECTL + 1`

            > 这里我不懂怎么会发生的???
            >
            > while ((sc = sizeCtl) >= 0){
            >
            > ​	if (sc < 0) {}
            >
            > }

        - 如果是第一个扩容，将`SIZECTL`设置为`resizeStamp(n) << RESIZE_STAMP_SHIFT) + 2`

```java
    private final void tryPresize(int size) {
        // 计算扩容的目标size
        // 给定的容量若>=MAXIMUM_CAPACITY的一半，直接扩容到允许的最大值，否则调用函数扩容
        int c = (size >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY :
            tableSizeFor(size + (size >>> 1) + 1);
        int sc;
        while ((sc = sizeCtl) >= 0) {// 没有正在初始化或扩容，或者说表还没有被初始化  
            Node<K,V>[] tab = table; int n;
            if (tab == null || (n = tab.length) == 0) {// 桶没被初始化，
                n = (sc > c) ? sc : c;// 取较大的容量
                 // 期间没有其他线程对表操作，则CAS将SIZECTL状态置为-1，表示正在进行初始化  
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                    try {
                        if (table == tab) {// 双检锁
                            @SuppressWarnings("unchecked")//初始化
                            Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                            table = nt;
                            sc = n - (n >>> 2);//无符号右移2位，此即0.75*n 
                        }
                    } finally {
                        sizeCtl = sc;// 释放锁
                    }
                }
            }
          	// 目标扩容size小于扩容阈值，或者容量超过最大限制时，不需要扩容
            else if (c <= sc || n >= MAXIMUM_CAPACITY)
                break;
            else if (tab == table) {// 需要扩容
                int rs = resizeStamp(n);
                if (sc < 0) {// sc<0表示，已经有其他线程正在扩容。但是怎么发生的？按理说
                    Node<K,V>[] nt;
               // 1. (sc >>> RESIZE_STAMP_SHIFT) != rs ：扩容线程数 > MAX_RESIZERS-1
               // 2. sc == rs + 1 和 sc == rs + MAX_RESIZERS ：表示什么？？？
               // 3. (nt = nextTable) == null ：表示nextTable正在初始化
               // transferIndex <= 0 ：表示所有hash桶均分配出去
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                        sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                        transferIndex <= 0)
                        break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                        transfer(tab, nt);
                }
   // 第一个执行扩容操作的线程，将sizeCtl设置为：(resizeStamp(n) << RESIZE_STAMP_SHIFT) + 2)
                else if (U.compareAndSwapInt(this, SIZECTL, sc,
                                             (rs << RESIZE_STAMP_SHIFT) + 2))
                    transfer(tab, null);
            }
        }
    }
```

### addCount

```java
// 如果<0，不检查调整大小，如果<= 1，只检查是否无竞争
private final void addCount(long x, int check) {
     CounterCell[] as; long b, s;
  
  	//利用CAS方法更新baseCount的值
     if ((as = counterCells) != null ||
         !U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) 
         CounterCell a; long v; int m;
         boolean uncontended = true;
         if (as == null || (m = as.length - 1) < 0 || // 随机更新一个cell的值
             (a = as[ThreadLocalRandom.getProbe() & m]) == null ||
             !(uncontended = U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
           	// 多线程 CAS 发生失败的时候执行
             fullAddCount(x, uncontended); 
             return;
         }
         if (check <= 1)
             return;
         s = sumCount();
     }
  	//如果check值大于等于0 则需要检验是否需要进行扩容操作，后面和tryPresize一个逻辑；
     if (check >= 0) {
         Node<K,V>[] tab, nt; int n, sc;
       	// 当条件满足的时候开始扩容
         while (s >= (long)(sc = sizeCtl) && (tab = table) != null &&
                (n = tab.length) < MAXIMUM_CAPACITY) {
             int rs = resizeStamp(n);
           	// 如果小于0 说明已经有线程在进行扩容了
             if (sc < 0) {
     // 一下的情况说明已经有在扩容或者多线程进行了扩容，其他线程直接 break 不要进入扩容
                 if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                     sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                     transferIndex <= 0)
                     break;
               	// 如果已经有其他线程在执行扩容操作
               	// 如果相等说明已经完成，可以继续扩容
                 if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                     transfer(tab, nt);
             }
           	// 当前线程是唯一的或是第一个发起扩容的线程  此时nextTable=null
    			// 这个时候 sizeCtl 已经等于(rs<<RESIZE_STAMP_SHIFT)+2 等于一个大的负数，这边
           	// 加上2很巧，因为 transfer 后面对 sizeCtl-- 操作的时候，最多只能减两个就结束
             else if (U.compareAndSwapInt(this, SIZECTL, sc,
                                          (rs << RESIZE_STAMP_SHIFT) + 2))
                 transfer(tab, null);
             s = sumCount();
         }
     }
 }
```

### fullAddCount

```java
private final void fullAddCount(long x, boolean wasUncontended) {
        int h;
        // 获取当前线程的 probe 值作为 hash 值,如果0则强制初始化当前线程的 Probe 值，
        // 初始化 probe 值不为 0
        if ((h = ThreadLocalRandom.getProbe()) == 0) {
            ThreadLocalRandom.localInit();      // force initialization
            h = ThreadLocalRandom.getProbe();
            // 设置未竞争标记为true
            wasUncontended = true;
        }
        boolean collide = false;                // True if last slot nonempty
        for (;;) {
            CounterCell[] as; CounterCell a; int n; long v;
            if ((as = counterCells) != null && (n = as.length) > 0) {
               // 如果counterCells数组对应位置上为null，创建一个cell，
                if ((a = as[(n - 1) & h]) == null) {
                    if (cellsBusy == 0) {            // Try to attach new Cell
                        CounterCell r = new CounterCell(x); // Optimistic create
                        if (cellsBusy == 0 &&
                            U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {// cellsBusy锁 
                            boolean created = false;
                            try {               // Recheck under lock
                                CounterCell[] rs; int m, j;
                                if ((rs = counterCells) != null &&
                                    (m = rs.length) > 0 &&
                                    rs[j = (m - 1) & h] == null) {// 双检锁
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                cellsBusy = 0;//释放锁
                            }
                            if (created)
                                break;
                            continue;           // Slot is now non-empty
                        }
                    }
                    collide = false;
                }
                // wasUncontended 为 false 说明已经发生了竞争，重置为true重新执行上面代码
                else if (!wasUncontended)       // CAS already known to fail
                    wasUncontended = true;      // Continue after rehash
               //如果对应位置上不为null，尝试更新value值
                else if (U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))
                    break;
                else if (counterCells != as || n >= NCPU)
                    collide = false;            // At max size or stale
                else if (!collide)
                    collide = true;
                 //如果对应不为null，并且更新失败，表示此时counterCells数组的容量过小，
                //此时需要扩容。
                else if (cellsBusy == 0 &&
                         U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                    try {
                        if (counterCells == as) {// Expand table unless stale
                            CounterCell[] rs = new CounterCell[n << 1];
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            counterCells = rs;
                        }
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue;                   // Retry with expanded table
                }
                h = ThreadLocalRandom.advanceProbe(h);
            }
            //初始化counterCells数组
            else if (cellsBusy == 0 && counterCells == as &&
                     U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                boolean init = false;
                try {                           // Initialize table
                    if (counterCells == as) {
                        CounterCell[] rs = new CounterCell[2];
                        rs[h & 1] = new CounterCell(x);
                        counterCells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                if (init)
                    break;
            }
            else if (U.compareAndSwapLong(this, BASECOUNT, v = baseCount, v + x))
                break;                          // Fall back on using base
        }
    }
```

### sumCount

```java
    public int size() {
        long n = sumCount();
        return ((n < 0L) ? 0 :
                (n > (long)Integer.MAX_VALUE) ? Integer.MAX_VALUE :
                (int)n);
	}
	public long mappingCount() {
        long n = sumCount();
        return (n < 0L) ? 0L : n; // ignore transient negative values
	}
	// 计算baseCount + CounterCell[].value的总值
	final long sumCount() {
        CounterCell[] as = counterCells; CounterCell a;
        long sum = baseCount;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null)
                    sum += a.value;
            }
        }
        return sum;
    }
```

### remove

> 删除的node节点的next依然指着下一个元素。此时若有一个遍历线程正在遍历这个已经删除的节点，这个遍历线程依然可以通过next属性访问下一个元素。从遍历线程的角度看，他并没有感知到此节点已经删除了，这说明了ConcurrentHashMap提供了弱一致性的迭代器。

-  获取扰动后的hash
- 如果桶未初始化或`(n - 1) & hash`位置不存在节点，结束
- 如果当前节点为***ForwardingNode***，协助扩容
- 如果当前节点存在且未在扩容，使用***synchronized***锁住头节点
    - 双检锁判断节点是否已被改动；
    - 当前节点为链表：遍历链表，找到便删除节点，删除的头节点便再更新头节点；
    - 当前节点为红黑树：
        - 使用*findTreeNode*查找节点
        - 找到再用*removeTreeNode*移除节点
        - 如果长度太短，链表化节点，更新头节点

```java
    public V remove(Object key) {
        return replaceNode(key, null, null);
    }
    // 当参数 value == null 时，删除节点。否则更新节点的值为value
    // cv 是个期望值，当 map[key].value 等于期望值 cv 或 cv == null 时，
    // 删除节点，或者更新节点的值
    final V replaceNode(Object key, V value, Object cv) {
        int hash = spread(key.hashCode());// 获取扰动后的hash
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0 ||
                (f = tabAt(tab, i = (n - 1) & hash)) == null)//桶未初始化或位置不存在节点
                break;
            else if ((fh = f.hash) == MOVED)// 当前节点为ForwardingNode，协助扩容
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                boolean validated = false;
                synchronized (f) {// 锁住当前节点
                    if (tabAt(tab, i) == f) {// 双检锁
                        if (fh >= 0) {// 当前节点为链表
                            validated = true;
                            for (Node<K,V> e = f, pred = null;;) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {// 找到相同的key
                                    V ev = e.val;// 获取旧值
                                    // 当 map[key].value 等于期望值 cv 或 cv == null 时，
   								 // 删除节点，或者更新节点的值
                                    if (cv == null || cv == ev ||
                                        (ev != null && cv.equals(ev))) {
                                        oldVal = ev;
                                        if (value != null)
                                            e.val = value;// 更新值
                                        else if (pred != null)// 删除非头节点
                                            pred.next = e.next;
                                        else
                                            setTabAt(tab, i, e.next);// 更新头节点
                                    }
                                    break;
                                }
                                pred = e;
                                if ((e = e.next) == null)// 到达链表尾部
                                    break;
                            }
                        }
                        else if (f instanceof TreeBin) {// 当前节点为红黑树
                            validated = true;
                            TreeBin<K,V> t = (TreeBin<K,V>)f;
                            TreeNode<K,V> r, p;
                            // 遍历红黑树
                            if ((r = t.root) != null &&
                                (p = r.findTreeNode(hash, key, null)) != null) {
                                V pv = p.val;
                                if (cv == null || cv == pv ||
                                    (pv != null && cv.equals(pv))) {
                                    oldVal = pv;
                                    if (value != null)
                                        p.val = value;// 更新值
                                    else if (t.removeTreeNode(p))// 删除节点
                                        // 判断是否需要链表化，更新头节点
                                        setTabAt(tab, i, untreeify(t.first));
                                }
                            }
                        }
                    }
                }
                if (validated) {
                    if (oldVal != null) {
                        if (value == null)
                            addCount(-1L, -1);//计数-1
                        return oldVal;//返回旧值
                    }
                    break;
                }
            }
        }
        return null;
    }
```

### get

```java
    public V get(Object key) {
        Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
        int h = spread(key.hashCode());// 获取扰动后的hash
        // 桶已初始化且当前位置存在节点；获取最新可见值
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (e = tabAt(tab, (n - 1) & h)) != null) {
            if ((eh = e.hash) == h) {// hash相等
                if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                    return e.val;// 存在相同的key
            }
            else if (eh < 0)// 当前为ForwardingNode或TreeBin
                return (p = e.find(h, key)) != null ? p.val : null;
            while ((e = e.next) != null) {// 遍历链表
                if (e.hash == h &&
                    ((ek = e.key) == key || (ek != null && key.equals(ek))))
                    return e.val;
            }
        }
        return null;
    }
```

### 总结

- 和JDK 1.8的HashMap一样的存储结构，采用数组+链表+红黑树；链表转红黑树的逻辑也和HashMap一致；
- 默认容量为16，扩容为*n = 2n*；

- hash桶懒加载； 

- 不允许Key和Value为null；

- 采用了***CAS***+***synchronized***的方式保证并发情况下的数据同步，锁粒度小，也保证了操作的原子性； 

- 多线程无锁扩容的关键就是通过***CAS***设置`sizeCtl`与`transferIndex`变量，协调多个线程对table数组中的Node进行迁移；



## 参考

[简书 × 谈谈ConcurrentHashMap1.7和1.8的不同实现](https://www.jianshu.com/p/e694f1e868ec)

[从ConcurrentHashMap的演进看Java多线程核心技术](http://www.jasongj.com/java/concurrenthashmap/)

[ConcurrentHashMap 源码解读](https://swenfang.github.io/2018/06/03/Java%208%20ConcurrentHashMap%20%E6%BA%90%E7%A0%81%E8%A7%A3%E8%AF%BB/)

[简书 × Java 源码分析-ConcurrentHashMap(1.8)](https://www.jianshu.com/p/c7f85caa0b3f)