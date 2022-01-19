# Java 容器

## 主要类库

![Container Taxonomy](..\Resources\Container Taxonomy.png)

以下均基于JDK 1.8

- Hashtable未继承 *AbstractMap* 类，但实现了 *Map* 接口，继承了 *Dictionary* 类；

1）List

- ArrayList ：Object 数组。
- Vector ：Object 数组。
- LinkedList ：双向链表(JDK6 之前为循环链表，JDK7 取消了循环)。

2）Map

- HashMap ：
    - JDK8 之前，HashMap 由数组+链表组成的，数组是HashMap的主体，链表则是主要为了解决哈希冲突而存在的（“拉链法”解决冲突）。
    - JDK8 以后，在解决哈希冲突时有了较大的变化，当链表长度大于阈值（默认为 8 ）时，将链表转化为红黑树，以减少搜索时间。
- LinkedHashMap ：LinkedHashMap 继承自 HashMap，所以它的底层仍然是基于拉链式散列结构即由数组和链表或红黑树组成。另外，LinkedHashMap 在上面结构的基础上，增加了一条双向链表，使得上面的结构可以保持键值对的插入顺序。同时通过对链表进行相应的操作，实现了访问顺序相关逻辑。详细可以查看：[《LinkedHashMap 源码详细分析（JDK1.8）》](https://www.imooc.com/article/22931) 。
- Hashtable ：数组+链表组成的，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突而存在的。
- TreeMap ：红黑树（自平衡的排序二叉树）。

3）Set

- HashSet ：无序，唯一，基于 HashMap 实现的，底层采用 HashMap 来保存元素。
- LinkedHashSet ：LinkedHashSet 继承自 HashSet，并且其内部是通过 LinkedHashMap 来实现的。有点类似于我们之前说的LinkedHashMap 其内部是基于 HashMap 实现一样，不过还是有一点点区别的。
- TreeSet ：有序，唯一，红黑树(自平衡的排序二叉树)。

## Hashcode的作用

1. hashCode的存在主要是用于查找的快捷性，如Hashtable，HashMap等，hashCode是用来在散列存储结构中确定对象的存储地址的；
2. 如果两个对象相同，就是适用于equals(java.lang.Object) 方法，那么这两个对象的hashCode一定要相同；
3. 如果对象的equals方法被重写，那么对象的hashCode也尽量重写，并且产生hashCode使用的对象，一定要和equals方法中使用的一致，否则就会违反上面提到的第2点；
4. 两个对象的hashCode相同，并不一定表示两个对象就相同，也就是不一定适用于equals(java.lang.Object) 方法，只能够说明这两个对象在散列存储结构中，如Hashtable，他们**“存放在同一个篮子里”**，但如果他们的hashCode不同哈希表的性能会更好（减少冲突）；

# Map

![HashMap](..\Resources\map-uml.png)

- Map即映射表，里面保存的是一组成对的”键值对”对象，一个映射不能包含重复的键，每个键最多只能映射到一个值，我们可以通过”键”找到该键对应的”值”。
- dentityHashMap：使用==代替equals对“键”进行比较的HashMap
- WeakHashMap：使用弱引用实现的HashMap，当其中的某些键值对不再被使用时会被自动GC掉

## HashMap

![HashMap](..\Resources\HashMap.png)

![HashMap Diagram](..\Resources\HashMap-UML.png)

### 属性

```java
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {
    /*--------------常量--------------*/
    // 默认hash桶初始长度16
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    // hash桶最大容量2的30次幂
    static final int MAXIMUM_CAPACITY = 1 << 30;
    // 默认加载因子为0.75f
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 链表的数量大于等于8个并且桶的数量大于等于64时链表树化 
  	static final int TREEIFY_THRESHOLD = 8;
  	// hash表某个节点链表的数量小于等于6时树拆分
  	static final int UNTREEIFY_THRESHOLD = 6;
  	// 树化时最小桶的数量
  	static final int MIN_TREEIFY_CAPACITY = 64;
  	/*--------------实例变量--------------*/
    // hash桶，长度必须为2的n次幂
  	transient Node<K,V>[] table;
    // 存放具体元素的集
    transient Set<Map.Entry<K,V>> entrySet;
  	// 键值对的数量
  	transient int size;
    // HashMap被改变的次数，用于fail-fast机制的实现
    transient int modCount;
    // 扩容的阀值，当键值对的数量超过这个阀值会产生扩容，threshold = table.length * loadFactor
    int threshold;
    // 负载因子, 用于计算哈希表元素数量的阈值
    final float loadFactor;
}
```

### Constructor

```java
    public HashMap() {
        // 默认构造函数，赋值加载因子为默认的0.75f, hash桶在put时初始化
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    // 同时指定初始化容量以及加载因子，用的很少，一般不会修改loadFactor
    public HashMap(int initialCapacity, float loadFactor) {
        // 初始化容量处理
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        // 加载因子判断
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        // 设置阈值为初始化容量的2的n次方的值
        this.threshold = tableSizeFor(initialCapacity);
    }
    // 新建一个哈希表，同时将另一个map m 里的所有元素加入表中
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }

```



```java
// 用来保证hash桶数量一定为2的n次方，哈希桶的实际容量 length。 返回值一般会>=cap 
static final int tableSizeFor(int cap) {
	// 经过下面的 或 和位移 运算， n最终各位都是1。‘|’为同为0时为0， 其他为1;
    int n = cap - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    // 判断n是否越界，返回 2的n次方作为 table（哈希桶）的阈值
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```


### 扰动函数

```java
// 可以看出HashMap的key可以为null
static final int hash(Object key) {
    int h;
    // key.hashCode()：返回散列值也就是hashcode
    // ^ ：按位异或
    // >>>:无符号右移，忽略符号位，空位都以0补齐
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

### 内部类 Node

```java
// 单链表    
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;// 存放的hash()的值
    final K key;
    V value;
    Node<K,V> next;
    Node(int hash, K key, V value, Node<K,V> next) {
        this.hash = hash;
        this.key = key;
        this.value = value;
        this.next = next;
   }
}
```

### 内部类 TreeNode

```java
// 红黑树
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;  // 父节点
    TreeNode<K,V> left;	   // 左节点
    TreeNode<K,V> right;   // 右节点
    TreeNode<K,V> prev;    // 双向链表前节点
    boolean red;		   //节点颜色
    TreeNode(int hash, K key, V val, Node<K,V> next) {
        super(hash, key, val, next);//最终为调用HashMap.Node的构造器
    }
}
```

### put

![HashMap′s put() flow](..\Resources\HashMap′s put() flow.jpg)

```java
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}
@Override
public V putIfAbsent(K key, V value) {
    return putVal(hash(key), key, value, true, true);
}
//onlyIfAbsent代表是否不覆盖，false为覆盖
final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    //tab存放 当前的hash桶， p用作临时链表节点  
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    //当table为空时，这里初始化table，不是通过构造函数初始化，而是在插入时通过扩容初始化，有效防止了初始化HashMap没有数据插入造成空间浪费可能造成内存泄露的情况
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    //没有发生哈希碰撞。 直接构建一个新节点Node存放
    if ((p = tab[i = (n - 1) & hash]) == null)//index = (n - 1) & hash
        tab[i] = newNode(hash, key, value, null);
    else {//发生了哈希冲突
        Node<K,V> e; K k;
        //如果头节点的hash值与key相等，直接覆盖
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        //当前节点为红黑树节点，转为红黑树插入
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {
        //当前节点为链表，循环遍历判定是否存在相同的key
            for (int binCount = 0; ; ++binCount) {
                //遍历到尾部，追加新节点到尾部
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    //链表的数量大于等于8个并且桶的数量大于等于64时链表转换为红黑树
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                //如果找到了要覆盖的节点
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        //如果e不是null，说明有需要覆盖的节点
        if (e != null) { 
            //旧值
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                //覆盖值
                e.value = value;
            //这是一个空实现的函数，用作LinkedHashMap重写使用。
            afterNodeAccess(e);
            //返回旧值
            return oldValue;
        }
    }
    //如果执行到了这里，说明插入了一个新的节点，所以会修改modCount & size，以及返回null。
    //map调整次数加1
    ++modCount;
    //更新size，并判断是否需要扩容。
    if (++size > threshold)
        resize();
    //这是一个空实现的函数，用作LinkedHashMap重写使用。
    afterNodeInsertion(evict);
    return null;
}
```

### 扩容函数

```java
	final Node<K,V>[] resize() {
		//oldTab 为当前表的哈希桶
        Node<K,V>[] oldTab = table;
        //当前哈希桶的容量 length
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        //当前的阈值
        int oldThr = threshold;
        //初始化新的容量和阈值为0
        int newCap, newThr = 0;
        //如果当前容量大于0
        if (oldCap > 0) {
            //如果当前容量已经到达上限
            if (oldCap >= MAXIMUM_CAPACITY) {
                //则设置阈值是2的31次方-1
                threshold = Integer.MAX_VALUE;
                //同时返回当前的哈希桶，不再扩容
                return oldTab;
            }//否则新的容量为旧的容量的两倍。 
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)//如果旧的容量大于等于默认初始容量16
                //那么新的阈值也等于旧的阈值的两倍
                newThr = oldThr << 1; // double threshold
        }//如果当前表是空的，但是有阈值。代表是初始化时指定了容量、阈值的情况
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;//那么新表的容量就等于旧的阈值
        else {}//如果当前表是空的，而且也没有阈值。代表是初始化时没有任何容量/阈值参数的情况               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;//此时新表的容量为默认的容量 16
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);//新的阈值为默认容量16 * 默认加载因子0.75f = 12
        }
        if (newThr == 0) {//如果新的阈值是0，对应的是  当前表是空的，但是有阈值的情况
            float ft = (float)newCap * loadFactor;//根据新表容量 和 加载因子 求出新的阈值
            //进行越界修复
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        //更新阈值 
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        //根据新的容量 构建新的哈希桶
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        //更新哈希桶引用
        table = newTab;
        //如果以前的哈希桶中有元素
        //下面开始将当前哈希桶中的所有节点转移到新的哈希桶中
        if (oldTab != null) {
            //遍历老的哈希桶
            for (int j = 0; j < oldCap; ++j) {
                //取出当前的节点 e
                Node<K,V> e;
                //如果当前桶中有元素,则将链表赋值给e
                if ((e = oldTab[j]) != null) {
                    //将原哈希桶置空以便GC
                    oldTab[j] = null;
                    //如果当前链表中就一个元素，（没有发生哈希碰撞）
                    if (e.next == null)
                        //直接将这个元素放置在新的哈希桶里。
                        //注意这里取下标 是用 哈希值 与 桶的长度-1 。 由于桶的长度是2的n次方，这么做其实是等于 一个模运算。但是效率更高
                        newTab[e.hash & (newCap - 1)] = e;
                    //当前节点是红黑树
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    //当前节点为链表
                    else { // preserve order
                        //因为扩容是容量翻倍，所以原链表上的每个节点，现在可能存放在原来的下标，即low位， 或者扩容后的下标，即high位。 high位=  low位+原哈希桶容量
                        //低位链表的头结点、尾节点
                        Node<K,V> loHead = null, loTail = null;
                        //高位链表的头节点、尾节点
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;//临时节点 存放e的下一个节点
                        do {
                            next = e.next;
                            //这里又是一个利用位运算 代替常规运算的高效点： 利用哈希值 与 旧的容量，可以得到哈希值去模后，是大于等于oldCap还是小于oldCap，等于0代表小于oldCap，应该存放在低位，否则存放在高位
                            if ((e.hash & oldCap) == 0) {
                                //给头尾节点指针赋值
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }//高位也是相同的逻辑
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }//循环直到链表结束
                        } while ((e = next) != null);
                        //将低位链表存放在原index处，
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        //将高位链表存放在新index处
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
```

### remove

```java
    public V remove(Object key) {
        Node<K,V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
            null : e.value;
    }
    @Override
    public boolean remove(Object key, Object value) {
        return removeNode(hash(key), key, value, true, true) != null;
    }
	// 如果参数matchValue是true，则必须key 、value都相等才删除。 
 	// 如果参数movable是false，在删除节点时，不移动其他节点
    final Node<K,V> removeNode(int hash, Object key, Object value,
                               boolean matchValue, boolean movable) {
        // p 是待删除节点的前置节点
        Node<K,V>[] tab; Node<K,V> p; int n, index;
        
        if ((tab = table) != null && (n = tab.length) > 0 &&//如果哈希表不为空
            (p = tab[index = (n - 1) & hash]) != null) {//根据hash值算出的index，且不为空
            //待删除节点
            Node<K,V> node = null, e; K k; V v;
            //如果头节点就是待删除节点
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                node = p;
            else if ((e = p.next) != null) {
                if (p instanceof TreeNode)//如果是红黑树
                    node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
                else {//如果是链表
                    do {//遍历获取待删除节点
                        if (e.hash == hash &&
                            ((k = e.key) == key ||
                             (key != null && key.equals(k)))) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
                }
            }
            //如果有待删除节点node，且 matchValue为false，或者值也相等
            if (node != null && (!matchValue || (v = node.value) == value ||
                                 (value != null && value.equals(v)))) {
                if (node instanceof TreeNode)//如果是红黑树，则以红黑树方式移除
                    ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
                else if (node == p)//如果为头节点
                    tab[index] = node.next;
                else//非头节点
                    p.next = node.next;
                //修改modCount & size
                ++modCount;
                --size;
                afterNodeRemoval(node);//LinkedHashMap回调函数
                return node;
            }
        }
        return null;
    }
```

### get

```java
    public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? defaultValue : e.value;
    }
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&//如果hash表不为空
            (first = tab[(n - 1) & hash]) != null) {//如果下标
            if (first.hash == hash && // 判断头节点
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                if (first instanceof TreeNode)//如果是红黑树
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {//如果是链表，遍历
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
```

### contains

```java
    //如果能通过key取出节点就代表存在key
	public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }
	//遍历链表获取值
    public boolean containsValue(Object value) {
        Node<K,V>[] tab; V v;
        if ((tab = table) != null && size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next) {
                    if ((v = e.value) == value ||
                        (value != null && value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }
```

### 红黑树

#### 基本规则

- 每个节点都只能是红色或者黑色
- 根节点是黑色
- 每个叶节点（NIL节点，空节点）是黑色的。
- 如果一个结点是红的，则它两个子节点都是黑的。
- 从任一节点到其每个叶子的所有路径都包含相同数目的黑色节点。
- 自平衡

#### put

```java
//#HashMap.TreeNode
		final TreeNode<K,V> putTreeVal(HashMap<K,V> map, Node<K,V>[] tab,
                                       int h, K k, V v) {
            Class<?> kc = null;
            boolean searched = false;
            TreeNode<K,V> root = (parent != null) ? root() : this;//获取根节点
            for (TreeNode<K,V> p = root;;) {
                int dir, ph; K pk;
                //判断当前节点的hash与给定的hash
                if ((ph = p.hash) > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                    return p;//已存在hash & key相同的节点，返回该节点
                else if ((kc == null &&
                          (kc = comparableClassFor(k)) == null) ||//返回key的类
                         (dir = compareComparables(kc, k, pk)) == 0) {//对比key值
                    //指定key没有实现comparable接口 或者 实现了comparable接口并且和当前节点的键对象比较之后相等（仅限第一次循环）
                    if (!searched) {//如果还没有比对过当前节点的所有子节点
                        TreeNode<K,V> q, ch;
                        searched = true;
                        if (((ch = p.left) != null &&
                             (q = ch.find(h, k, kc)) != null) ||
                            ((ch = p.right) != null &&
                             (q = ch.find(h, k, kc)) != null))
                            return q;
                    }
                    // 走到这里就说明，遍历了所有子节点也没有找到和当前键equals相等的节点
                    dir = tieBreakOrder(k, pk);// 再比较一下当前节点键和指定key键的大小
                }
				//遍历找到要添加的方向的空节点
                TreeNode<K,V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    Node<K,V> xpn = xp.next;
                    TreeNode<K,V> x = map.newTreeNode(h, k, v, xpn);// 创建一个新的树节点
                    //根据dir插入相应的方向
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    xp.next = x;//链表中的next节点指向到这个新的树节点
                    x.parent = x.prev = xp;// 这个新的树节点的父节点、前节点均设置为 当前的树节点
                    if (xpn != null)//如果原来的next节点不为空
                        ((TreeNode<K,V>)xpn).prev = x;// 那么原来的next节点的前节点指向到新的树节点
                    moveRootToFront(tab, balanceInsertion(root, x));// 重新平衡，以及新的根节点置顶
                    return null;// 返回空，意味着产生了一个新节点
                }
            }
        }
```

#### get

```java
        //从根节点开始遍历寻找hash&key值相等的节点
        final TreeNode<K,V> getTreeNode(int h, Object k) {
            return ((parent != null) ? root() : this).find(h, k, null);
        }
        final TreeNode<K,V> find(int h, Object k, Class<?> kc) {
            TreeNode<K,V> p = this;//从调用对象为根节点开始遍历
            do {
                int ph, dir; K pk;
                TreeNode<K,V> pl = p.left, pr = p.right, q;
                //如果当前节点的hash大于寻找的hash，则偏向左子树，小于则偏向右子树
                if ((ph = p.hash) > h)
                    p = pl;
                else if (ph < h)
                    p = pr;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                    return p;//如果hash&key相等，则返回当前节点
                else if (pl == null)//如果hash相等，则偏向不为空的子树
                    p = pr;
                else if (pr == null)
                    p = pl;
                else if ((kc != null ||//如果hash相等，且子树都不为空
                          (kc = comparableClassFor(k)) != null) &&//kc是否是一个可比较的类
                         (dir = compareComparables(kc, k, pk)) != 0)//比较k和p.key
                    p = (dir < 0) ? pl : pr;//k<p.key向左子树移动否则向右子树移动
                else if ((q = pr.find(h, k, kc)) != null)//检查右子树
                    return q;
                else
                    p = pl;//检查左子树
            } while (p != null);
            return null;
        }
```

#### remove

```java
        /** 在红黑树中删除当前对象的节点
         * 1.判断当前是否空链；
         * 2.删除双向链表中的该节点；
         * 3.判断节点数量是否过少，如果过少，将红黑树转换为单向链表；
         * 4.删除红黑树节点：
         *		1) 当前节点是叶子节点，直接删除；
         * 		2) 当前节点只有一个子节点，删除节点，以子节点替代；
         * 		3) 当前节点有两个子节点，找到右子树中最小元素作为后继节点；将后继节点信息替换到
         *    	   当前节点，因为后继节点至多只有右子树，以1.2.处理后继节点；
         * 5.修复红黑树性质；
         * 6.使双向链表以红黑树的根节点为头节点；
         */
		final void removeTreeNode(HashMap<K,V> map, Node<K,V>[] tab,
                                  boolean movable) {
            int n;
            if (tab == null || (n = tab.length) == 0)//空判断
                return;
            int index = (n - 1) & hash;
            /*-------更改TreeNode的双向链表-------*/
            TreeNode<K,V> first = (TreeNode<K,V>)tab[index], root = first, rl;
            //succ指向要删除结点的后一个点，pred指向要删除结点的前一个
            TreeNode<K,V> succ = (TreeNode<K,V>)next, pred = prev;
            //若要删除的结点的前一个为空，则first和tab[index]都指向要删除结点的后一个结点
            if (pred == null)
                tab[index] = first = succ;
            else//若要删除结点的前驱非空，则前一个结点的next指针指向该结点的后驱
                pred.next = succ;
            if (succ != null)//后驱结点不为空时，后驱结点的前置指针设为删除结点的前置结点
                succ.prev = pred;
            if (first == null)//若删除的结点是树中的唯一结点则直接结束
                return;
            if (root.parent != null)
                root = root.root();//确保root指向根结点
            // 节点过少，转为单向链表
            if (root == null
                || (movable
                    && (root.right == null
                        || (rl = root.left) == null
                        || rl.left == null))) {
                tab[index] = first.untreeify(map);  // too small
                return;
            }
            //p指向要删除的结点
            TreeNode<K,V> p = this, pl = left, pr = right, replacement;
            //删除结点的左右子树都不为空时，寻找右子树中最左的叶结点作为后继，s指向这个后继结点
            if (pl != null && pr != null) {
                /* 寻找当前节点的最小节点，并与之交换位置信息，
                 * 最后用replacement替换当前节点，并移除p；
                 */
                TreeNode<K,V> s = pr, sl;
                while ((sl = s.left) != null) // find successor
                    s = sl;
                //交换后继结点和要删除结点的颜色
                boolean c = s.red; s.red = p.red; p.red = c; // swap colors
                TreeNode<K,V> sr = s.right;
                TreeNode<K,V> pp = p.parent;
                //s是p的直接右儿子，交换p和s的位置
                if (s == pr) { // p was s's direct parent
                    p.parent = s;
                    s.right = p;
                }
                else {
                    TreeNode<K,V> sp = s.parent;
                    //p放到s原本的位置
                    if ((p.parent = sp) != null) {
                        if (s == sp.left)
                            sp.left = p;
                        else
                            sp.right = p;
                    }
                    //s放到p原本的位置
                    if ((s.right = pr) != null)
                        pr.parent = s;
                }
                p.left = null;
                if ((p.right = sr) != null)
                    sr.parent = p;//s原本的右子树成为p的右子树
                if ((s.left = pl) != null)
                    pl.parent = s;//s原本的左子树成为p的左子树
                if ((s.parent = pp) == null)
                    root = s;//若p原本是根则新的根是s
                else if (p == pp.left)
                    pp.left = s;//若p是某个结点的左儿子，则s成为该结点的左儿子
                else
                    pp.right = s;//若p是某个结点的右儿子，则s成为该结点的右儿子
                if (sr != null)//若s结点有右儿子（s一定没有左儿子），则replacement为这个右儿子否则为p
                    replacement = sr;
                else
                    replacement = p;
            }
            else if (pl != null)//若s结点有右儿子（s一定没有左儿子），则replacement为这个右儿子否则为p
                replacement = pl;
            else if (pr != null)
                replacement = pr;
            else
                replacement = p;
            if (replacement != p) {//p有儿子或者s有儿子
                TreeNode<K,V> pp = replacement.parent = p.parent;
                if (pp == null)//用replacement来替换p
                    root = replacement;
                else if (p == pp.left)
                    pp.left = replacement;
                else
                    pp.right = replacement;
                p.left = p.right = p.parent = null;//移除p结点
            }
			//以replacement为中心，进行红黑树性质的修复，replacement可能为s的右儿子或者p的儿子或者p自己
            TreeNode<K,V> r = p.red ? root : balanceDeletion(root, replacement);

            if (replacement == p) {  // detach
                TreeNode<K,V> pp = p.parent;
                p.parent = null;
                if (pp != null) {
                    if (p == pp.left)
                        pp.left = null;
                    else if (p == pp.right)
                        pp.right = null;
                }
            }
            if (movable)
                moveRootToFront(tab, r);
        }
```

## 总结
**HashMap**的存储结构为`数组Node[]`+`单向链表Node`+`红黑树TreeNode`；

默认容量为`16`，容器hash桶使用`懒加载`方式，扩容方式为`newCap = oldCap << 1`；

默认加载因子为`0.75f`，扩容阀值为`容量 * 加载因子`，扩容时阀值也为2倍：`newThr = oldThr << 1`;

当链表长度超过8且hash桶容量小于64时，hash桶扩容；

当链表长度超过8且hash桶容量大于64时，链表红黑树化；

hash计算方式为 `hash = key.hashCode() ^ (key.hashCode() >>> 16)`;

因为hash计算时对null做了特殊处理，HashMap允许key与value为null；

index计算方式为 `index = (table.length - 1) & hash`;

扩容为创建新容量的hash桶，并复制迁移原hash桶的节点；

扩容时，将单节点以`index = (newCap - 1) & hash`放入新桶中；将链表以`hash & oldCap == 0`保留原顺序分为两个节点，符合条件的在原位置`index`，不相等的放置到`index + oldCap`；将红黑树也以`hash & oldCap == 0`的方式分为两个Node，同时Node长度小于等于6的解除红黑树，转为链表，长度大于6的仍然构造为红黑树；

## LinkedHashMap

### 属性

```java
public class LinkedHashMap<K,V>
    extends HashMap<K,V>
    implements Map<K,V>{
    // 双向链表的头节点，使用最老的对象
    transient LinkedHashMap.Entry<K,V> head;
    // 双向链表的尾节点，使用最新的对象
    transient LinkedHashMap.Entry<K,V> tail;
    // 默认是false，则迭代时输出的顺序是插入节点的顺序。
    // 若为true，则输出的顺序是按照访问节点的顺序。即调用过get的对象移到尾部
    final boolean accessOrder;
}
```

### Constructor

```java
    public LinkedHashMap() {
        super();
        accessOrder = false;
    }
    public LinkedHashMap(int initialCapacity) {
        super(initialCapacity);
        accessOrder = false;
    }
    public LinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        accessOrder = false;
    }
    public LinkedHashMap(int initialCapacity,float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }
    public LinkedHashMap(Map<? extends K, ? extends V> m) {
        super();
        accessOrder = false;
        putMapEntries(m, false);
    }
```

### 内部类 Entry

```java
    // 将HashMap的Node单向链表扩展为双向链表
	static class Entry<K,V> extends HashMap.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    }

    private void transferLinks(LinkedHashMap.Entry<K,V> src,
                               LinkedHashMap.Entry<K,V> dst) {
        LinkedHashMap.Entry<K,V> b = dst.before = src.before;
        LinkedHashMap.Entry<K,V> a = dst.after = src.after;
        if (b == null)
            head = dst;
        else
            b.after = dst;
        if (a == null)
            tail = dst;
        else
            a.before = dst;
    }
```

### put

***LinkedHashMap***并没有重写任何put方法。但是其重写了构建新节点的*newNode()*方法.
*newNode()*会在***HashMap***的*putVal()*方法里被调用，*putVal()*方法会在批量插入数据*putMapEntries(Map<? extends K, ? extends V> m, boolean evict)*或者插入单个数据*public V put(K key, V value)*时被调用。

```java
	// HashMap newNode 中实现
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        return new Node<>(hash, key, value, next);
    }
	// 重写了HashMap的newNode方法；
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
        LinkedHashMap.Entry<K,V> p =
            new LinkedHashMap.Entry<K,V>(hash, key, value, e);
        linkNodeLast(p);
        return p;
    }
	// 链接到最后
    private void linkNodeLast(LinkedHashMap.Entry<K,V> p) {
        LinkedHashMap.Entry<K,V> last = tail;
        tail = p;
        if (last == null)
            head = p;
        else {
            p.before = last;
            last.after = p;
        }
    }
```

### get

```java
    public V get(Object key) {
        Node<K,V> e;
        if ((e = getNode(hash(key), key)) == null)
            return null;
        if (accessOrder)
            afterNodeAccess(e);
        return e.value;
    }
	// 将当前节点移动到节点尾部，在get,put已存在的键,replace中调用
    void afterNodeAccess(Node<K,V> e) { // move node to last
        LinkedHashMap.Entry<K,V> last;
        if (accessOrder && (last = tail) != e) {
            LinkedHashMap.Entry<K,V> p =
                (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
            p.after = null;
            if (b == null)
                head = a;
            else
                b.after = a;
            if (a != null)
                a.before = b;
            else
                last = b;
            if (last == null)
                head = p;
            else {
                p.before = last;
                last.after = p;
            }
            tail = p;
            ++modCount;
        }
    }
```

### remove

***LinkedHashMap***也没有重写*remove()*方法，但它重写了*afterNodeRemoval()*这个回调方法。该方法会在*removeNode()*方法中回调，*removeNode()*会在所有涉及到删除节点的方法中被调用;

```java
	//  从双向链表中删除对应的节点;
	void afterNodeRemoval(Node<K,V> e) { // unlink
        LinkedHashMap.Entry<K,V> p =
            (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
        p.before = p.after = null;// 便于GC
        if (b == null)
            head = a;
        else
            b.after = a;
        if (a == null)
            tail = b;
        else
            a.before = b;
    }
```

### contains

```java
    // 由于双向链表的存在，遍历效率比HashMap高得多
	public boolean containsValue(Object value) {
        for (LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after) {
            V v = e.value;
            if (v == value || (value != null && value.equals(v)))
                return true;
        }
        return false;
    }
```

### LRU

> LRU 是 Least Recently Used 的简称，即近期最少使用;
>
> **LRU 算法实现的关键就像它名字一样，当达到预定阈值的时候，这个阈值可能是内存不足，或者容量达到最大，找到最近最少使用的存储元素进行移除，保证新添加的元素能够保存到集合中。**

```java
    void afterNodeInsertion(boolean evict) { // hashMap中的方法传入的值大部分都为true
        LinkedHashMap.Entry<K,V> first;
        if (evict && (first = head) != null && removeEldestEntry(first)) {
            K key = first.key;
            removeNode(hash(key), key, null, false, true);
        }
    }
	// LinkedHashMap 默认返回 false 则不删除节点。
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return false;
    }
```



## Hashtable

![Hashtable-UML](..\Resources\Hashtable-UML.png)

### 属性

```java
public class Hashtable<K,V>
    extends Dictionary<K,V>
    implements Map<K,V>, Cloneable, java.io.Serializable {
    // hash桶
    private transient Entry<?,?>[] table;
    // 键值对数量，相当于HashMap的size
    private transient int count;
    // 阈值，用于判断是否需要调整Hashtable的容量（threshold = 容量*加载因子）
    private int threshold;
    // 加载因子，默认为0.75f
    private float loadFactor;
    // Hashtable被改变的次数，用于fail-fast机制的实现
    private transient int modCount = 0;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    // Hashtable的“key的集合”。
    private transient volatile Set<K> keySet;
    // Hashtable的“key-value的集合”。
    private transient volatile Set<Map.Entry<K,V>> entrySet;
    // Hashtable的“value的集合”。可以有重复元素
    private transient volatile Collection<V> values;
    // 类型枚举
    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int ENTRIES = 2;
}
```

### Constructor

```java
    public Hashtable() {
        // 表明默认hash桶大小为11，加载因子为0.75f。
        this(11, 0.75f);
    }
	public Hashtable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }    
	public Hashtable(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal Load: "+loadFactor);
		
        if (initialCapacity==0)
            initialCapacity = 1;
        this.loadFactor = loadFactor;
        /* 可以看出Hashtable并没有像HashMap采用tableSizeFor()保证桶长度一定为2的次方
         * 且hash桶在最开始就已经初始化 */
        table = new Entry<?,?>[initialCapacity];
        threshold = (int)Math.min(initialCapacity * loadFactor, MAX_ARRAY_SIZE + 1);
    }
    public Hashtable(Map<? extends K, ? extends V> t) {
        this(Math.max(2*t.size(), 11), 0.75f);
        putAll(t);
    }
```

### 内部类 Entry

```java
	// 和HashMap的Node一样的单向链表
    private static class Entry<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Entry<K,V> next;
        protected Entry(int hash, K key, V value, Entry<K,V> next) {
            this.hash = hash;
            this.key =  key;
            this.value = value;
            this.next = next;
        }
    }
```

### put

```java
	// 用sychronized修饰了put方法体
	public synchronized V put(K key, V value) {
        // 值不允许为空
        if (value == null) {
            throw new NullPointerException();
        }
        // 因为对象初始化时table已经初始化，不需要如HashMap一样调用扩容函数
        Entry<?,?> tab[] = table;
        // 可以看出hashtable未对key.hash()做扰动处理,且当key==null时会抛出异常即key不能为null
        int hash = key.hashCode();
        // index获取方式
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> entry = (Entry<K,V>)tab[index];
        // 存在hash与key值相等的key时，直接覆盖且返回旧值
        for(; entry != null ; entry = entry.next) {
            if ((entry.hash == hash) && entry.key.equals(key)) {
                V old = entry.value;
                entry.value = value;
                return old;
            }
        }
		// 添加新键值对
        addEntry(hash, key, value, index);
        return null;
    }
    private void addEntry(int hash, K key, V value, int index) {
        modCount++;
        Entry<?,?> tab[] = table;
        // 判断是否需要扩容
        if (count >= threshold) {
            rehash();
            tab = table;
            hash = key.hashCode();
            // 重新计算index
            index = (hash & 0x7FFFFFFF) % tab.length;
        }
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>) tab[index];
        // 头插法
        tab[index] = new Entry<>(hash, key, value, e);
        count++;
    }
```

### 扩容函数

```java
    protected void rehash() {
        // 获取旧容量与旧桶
        int oldCapacity = table.length;
        Entry<?,?>[] oldMap = table;

        // 扩容方式为2n + 1
        int newCapacity = (oldCapacity << 1) + 1;
        // 特殊值处理
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            if (oldCapacity == MAX_ARRAY_SIZE)
                // Keep running with MAX_ARRAY_SIZE buckets
                return;
            newCapacity = MAX_ARRAY_SIZE;
        }
        // 创建新桶
        Entry<?,?>[] newMap = new Entry<?,?>[newCapacity];
        modCount++;
        // 更新扩容阈值，并引用新桶
        threshold = (int)Math.min(newCapacity * loadFactor, MAX_ARRAY_SIZE + 1);
        table = newMap;
		// 倒序遍历并将原元素计算新的index放入新桶中
        for (int i = oldCapacity ; i-- > 0 ;) {
            for (Entry<K,V> old = (Entry<K,V>)oldMap[i] ; old != null ; ) {
                Entry<K,V> e = old;
                old = old.next;
				// 获取新的index，并头插法插入新桶
                int index = (e.hash & 0x7FFFFFFF) % newCapacity;
                e.next = (Entry<K,V>)newMap[index];
                newMap[index] = e;
            }
        }
    }
```

### remove

```java
    // 用sychronized修饰了remove方法体
	public synchronized V remove(Object key) {
        // 获取hash与index
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>)tab[index];
        // 遍历链表删除元素
        for(Entry<K,V> prev = null ; e != null ; prev = e, e = e.next) {
            if ((e.hash == hash) && e.key.equals(key)) {
                modCount++;
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                count--;
                V oldValue = e.value;
                e.value = null;
                return oldValue;
            }
        }
        return null;
    }
```

### get

```java
    // 用sychronized修饰了get方法体
	// 获取hash计算index，遍历链表
	public synchronized V get(Object key) {
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for (Entry<?,?> e = tab[index] ; e != null ; e = e.next) {
            if ((e.hash == hash) && e.key.equals(key)) {
                return (V)e.value;
            }
        }
        return null;
    }
```

### contains

```java
    public boolean containsValue(Object value) {
        return contains(value);
    }
	// 用sychronized修饰了contains方法体
    public synchronized boolean contains(Object value) {
        // 处理null
        if (value == null) {
            throw new NullPointerException();
        }
		// 暴力遍历判断value是否存在
        Entry<?,?> tab[] = table;
        for (int i = tab.length ; i-- > 0 ;) {
            for (Entry<?,?> e = tab[i] ; e != null ; e = e.next) {
                if (e.value.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
	// 用sychronized修饰了containsKey方法体
    public synchronized boolean containsKey(Object key) {
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        //通过hash获取index，获取对应的链表，遍历链表
        for (Entry<?,?> e = tab[index] ; e != null ; e = e.next) {
            if ((e.hash == hash) && e.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
```

## TreeMap

![TreeMap-UML](..\Resources\TreeMap-UML.png)

### 属性

- *TreeMap* 实现了*NavigableMap*接口，意味着它**支持一系列的导航方法。**比如返回有序的key集合。

```java
public class TreeMap<K,V> extends AbstractMap<K,V> 
	implements NavigableMap<K,V>, Cloneable, java.io.Serializable{
    // 比较器
	private final Comparator<? super K> comparator;
	// 红黑树根节点
	private transient Entry<K,V> root = null;
	// 集合元素数量
	private transient int size = 0;
	// TreeMap结构改变次数，用于fail-fast机制的实现
	private transient int modCount = 0;
	/* 用于导航的Set与Map */
	private transient EntrySet entrySet;
    private transient KeySet<K> navigableKeySet;
    private transient NavigableMap<K,V> descendingMap;
	// 红黑树颜色枚举
	private static final boolean RED   = false;
    private static final boolean BLACK = true;
}
```

### Constructor

```java
    // 代表使用key的自然顺序来维持TreeMap的顺序，这里要求key必须实现Comparable接口
	public TreeMap() {
        comparator = null;
    }
	// 用指定的比较器构造一个TreeMap
    public TreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }
	// 构造一个指定map的TreeMap，同样比较器comparator为空，使用key的自然顺序排序
    public TreeMap(Map<? extends K, ? extends V> m) {
        comparator = null;
        putAll(m);
    }
	// 构造一个指定SortedMap的TreeMap，根据SortedMap的比较器来维持TreeMap的顺序
    public TreeMap(SortedMap<K, ? extends V> m) {
        comparator = m.comparator();
        try {
            buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }
    }
```

### 内部类 Entry

```java
    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        // 左子树
        Entry<K,V> left;
        // 右子树
        Entry<K,V> right;
        // 父节点
        Entry<K,V> parent;
        boolean color = BLACK;
        //用key，value和父节点构造一个Entry，默认为黑色
        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
    }
```

经过以上可以看出TreeMap，key不可以为null，数据结构为红黑树；

### put

- 如果没有根节点，直接创建为根节点
- 以比较器比较key，找到插入的位置
    - 如果当前位置不为空，覆盖
    - 为空，插入当前位置
        - 重新平衡红黑树

```java
    public V put(K key, V value) {
        Entry<K,V> t = root;
        // 如果没有根节点存在，直接新建为根节点
        if (t == null) {
            compare(key, key); // 类型检查
            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        // 记录比较结果，如果cmp为负数，插入左子树
        int cmp;
        // 记录要插入的节点的父节点
        Entry<K,V> parent;
        // 获取当前使用的比较器
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {//有指定的比较器
            /* 如果新的key值小，往左子树查询；
             * 如果新的key值大，往右子树查询；
             * 如果key值相等，覆盖值
             * 直到当前节点为空，则为插入的位置
             */
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        else {
            // key不可为空，并且必须实现了Comparable接口
            if (key == null)
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
            //逻辑和指定的比较器相同
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        // 再找到的父节点下插入
        Entry<K,V> e = new Entry<>(key, value, parent);
        // 如果cmp为负数，插入左子树
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        // 保持红黑树平衡，对红黑树进行调整
        fixAfterInsertion(e);
        size++;
        modCount++;
        return null;
    }
```

### remove

```java
	public V remove(Object key) {
        Entry<K,V> p = getEntry(key);
        if (p == null)
            return null;
        V oldValue = p.value;
        deleteEntry(p);
        return oldValue;
    }
	    /* 删除红黑树节点：
         *   1) 当前节点是叶子节点，直接删除；
         * 	 2) 当前节点只有一个子节点，删除节点，以子节点替代；
         * 	 3) 当前节点有两个子节点，找到右子树中最小元素作为后继节点；将后继节点信息替换到
         *    	当前节点，因为后继节点至多只有右子树，以1.2.处理后继节点；
         */
    private void deleteEntry(Entry<K,V> p) {
        modCount++;
        size--;
        // 当前节点有两个子节点，找到右子树中最小元素作为后继节点；将后继节点信息替换到当前节点
        if (p.left != null && p.right != null) {
            Entry<K,V> s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
            // 后继节点至多只有右子树
        } 
        // replacement为替代节点p的继承者
        Entry<K,V> replacement = (p.left != null ? p.left : p.right);
        if (replacement != null) {// 替代节点不是叶子节点
            // 将替代节点的位置给予继承者
            replacement.parent = p.parent;
            if (p.parent == null)
                root = replacement;
            else if (p == p.parent.left)
                p.parent.left  = replacement;
            else
                p.parent.right = replacement;
			// 删除替代节点
            p.left = p.right = p.parent = null;
            // 如果替代节点为黑色，修复红黑树性质
            if (p.color == BLACK)
                fixAfterDeletion(replacement);
        } else if (p.parent == null) { // 替代节点是叶子节点，且没有父节点，即其为根节点
            root = null;//删除根节点
        } else { // 替代节点是叶子节点，且有父节点
            if (p.color == BLACK)// 如果替代节点为黑色，修复红黑树性质
                fixAfterDeletion(p);
			// 删除替代节点p
            if (p.parent != null) {
                if (p == p.parent.left)
                    p.parent.left = null;
                else if (p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
    }
```

### get

```java
    //以比较器结果左右查询子树
	public V get(Object key) {
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
    }
    final Entry<K,V> getEntry(Object key) {
        // Offload comparator-based version for sake of performance
        if (comparator != null)
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = k.compareTo(p.key);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return p;
        }
        return null;
    }
    final Entry<K,V> getEntryUsingComparator(Object key) {
        @SuppressWarnings("unchecked")
            K k = (K) key;
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            Entry<K,V> p = root;
            while (p != null) {
                int cmp = cpr.compare(k, p.key);
                if (cmp < 0)
                    p = p.left;
                else if (cmp > 0)
                    p = p.right;
                else
                    return p;
            }
        }
        return null;
    }
```



## 红黑树平衡

### 新增后平衡

- 将新插入的节点设置为红色
- 如果当前为根节点，将其置为黑色；
- 如果当前的父节点是黑色，因为不会对路径上的黑色节点数量有影响，不再做处理
- 如果当前的父节点是红色，则祖父节点一定为黑
    - 如果叔叔节点是红色
      
        - 将父节点与叔节点置为黑色，祖父节点置为红色，然后将祖父节点作为插入节点，递归判断
        
        ![RBTree Insert Case - 1](..\Resources\RBTree Insert Case - 1.png)
        
    - 如果叔叔节点是黑色或空节点（以下基于父节点为祖父节点的左子树）
      
        - 如果插入位置为左子树
            - 将祖父节点右旋
            - 将父节点设置为黑色
            - 将祖父节点设置为红色
        
        ![RBTree Insert Case - 2](..\Resources\RBTree Insert Case - 2.png)
        
        - 如果插入位置为右子树
            - 将父节点左旋
            - 然后交换当前节点与父节点身份（形成上方情况）
        
        ![RBTree Insert Case - 3](..\Resources\RBTree Insert Case - 3.png)

> 以下为JDK 1.8 的TreeMap的插入后平衡方法源码；
>
> HashMap的红黑树TreeNode的插入后平衡方法 *balanceInsertion* 采用相同的逻辑

```java
	private void fixAfterInsertion(Entry<K,V> x) {
        // 先将颜色改为红色
        x.color = RED;
		// 父节点是黑色，不再做处理
        while (x != null && x != root && x.parent.color == RED) {
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {// 父节点为祖父节点的左子树
                Entry<K,V> y = rightOf(parentOf(parentOf(x)));//叔叔节点
                if (colorOf(y) == RED) {// 叔叔节点是红色
                    // 将父与叔节点置为黑色，祖父节点置为红色
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    // 将祖父节点作为插入节点，递归判断
                    x = parentOf(parentOf(x));
                } else {
                    if (x == rightOf(parentOf(x))) {// 插入位置为右子树
                        // 将x设置为父节点后左旋，左旋后x即为插入节点的左子树
                        x = parentOf(x);
                        rotateLeft(x);
                    }
                    setColor(parentOf(x), BLACK);// 父节点设置为黑色
                    setColor(parentOf(parentOf(x)), RED);// 祖父节点设置为红色
                    rotateRight(parentOf(parentOf(x)));// 将祖父节点右旋
                }
            } else {// 父节点为祖父节点的右子树
                Entry<K,V> y = leftOf(parentOf(parentOf(x)));//叔叔节点
                if (colorOf(y) == RED) {// 叔叔节点是红色
                    // 将父与叔节点置为黑色，祖父节点置为红色
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    // 将祖父节点作为插入节点，递归判断
                    x = parentOf(parentOf(x));
                } else {
                    if (x == leftOf(parentOf(x))) {// 插入位置为左子树
                        // 将x设置为父节点后右旋，右旋后x即为插入节点的右子树
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    setColor(parentOf(x), BLACK);// 父节点设置为黑色
                    setColor(parentOf(parentOf(x)), RED);// 祖父节点设置为红色
                    rotateLeft(parentOf(parentOf(x)));// 将祖父节点左旋
                }
            }
        }
        // 将根节点置为黑色
        root.color = BLACK;
    }
```

### 删除后平衡

- 以下均基于以当前节点的路径被删除了一个黑色节点

    - 当前节点为红色，将当前节点置为黑色即可

    - 当前节点为黑色（以下基于为父节点的左子树）

        - 如果兄弟节点不存在，将父节点 P 看作新的当前节点 N处理
        - 兄弟节点为红色，则父节点、兄弟节点的子树均为黑色
            - 将父节点置为红色，兄弟节点置为黑色
            - 左旋父节点
        - 转向其他处理方式
        

![RBTree Delete Case - 1](..\Resources\RBTree Delete Case - 1.png)
        
- 兄弟节点 B 为黑色，P为任意颜色
        
  - BR为黑色，BL为黑色
    
          - 将兄弟节点B改为红色；此时经过P的路径都少了一个黑色
          - 将P 看作新的 N 继续处理
      
      
      ​    
      ​    ![RBTree Delete Case - 4](..\Resources\RBTree Delete Case - 4.png)
      
      - BR为黑色，BL为红色
          - 交换B 和 BL的颜色
            - 右旋节点B
            - 转为下方的处理方式
            
          
          
        
        ![RBTree Delete Case - 3](..\Resources\RBTree Delete Case - 3.png)
        
      - BR为红色，BL为任意颜色
           - 将兄弟节点B 置为 父节点P 的颜色；
             - 将父节点 置为 黑色；
             	 	   - 将 BR 置为黑色；
             - 左旋父节点 P
             ![RBTree Delete Case - 2](..\Resources\RBTree Delete Case - 2.png)       

> JDK 1.8的HashMap的红黑树TreenNode的*balanceDeletion*以及TreeMap的*fixAfterDeletion*均采用以上逻辑处理

## Map总结

**HashMap**

- 存储结构为数组 + 单向链表 + 红黑树（树内部维护了一个双向链表）；
- 默认容量为16；
- 扩容方式为 *n = 2n*;
- 继承 *AbstractMap* 类；
- 非线程安全；
- *index = (n - 1) & (hash ^ (hash >>> 16));*
- key与vaule均可以为null；
- hash桶懒加载； 

**LinkedHashMap**

- 基于HashMap实现；额外维护了一个双向链表；
- 通过构造参数 accessOrder 来指定双向链表是否在元素被访问后改变其在双向链表中的位置；
    - 当accessOrder为默认值false时，元素的遍历顺序与元素的插入顺序相同

**Hashtable**

- 存储结构为数组 + 单向链表；
- 默认容量为11；
- 扩容方式为 *n = 2n + 1*;
- 继承 *Dictionary* 类；
- 线程安全；
- *index =  (hash & 0x7FFFFFFF) % n*；
- key与value均不可以为null；
- hash桶在对象初始化时初始化；

**TreeMap**

- 存储结构为红黑树；
- 实现了 *NavigableMap* 接口；
- 该映射根据其键的自然顺序进行排序，或者根据创建映射时提供的 Comparator进行排序

# List

![List-UML](..\Resources\List-UML.png)

> *RandomAccess* 标识接口，标识列表能够快速随机访问存储的元素

## ArrayList

![ArrayList-UML](..\Resources\ArrayList-UML.png)

> 以下均基于JDK 1.8;

### 属性

```java
public class ArrayList<E> extends AbstractList<E>
	implements List<E>, RandomAccess, Cloneable, java.io.Serializable{
	// 保存元素的数组
	transient Object[] elementData; // non-private to simplify nested class access
	// 默认容量
	private static final int DEFAULT_CAPACITY = 10;
	// 空数组
	private static final Object[] EMPTY_ELEMENTDATA = {};
	// 默认构造器时懒加载空数组
	private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
	// 实际大小
	private int size;
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
}
```

### Constructor

```java
    public ArrayList(int initialCapacity) {
        // 根据传入的设定容量初始化数组
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }
    public ArrayList() {
        // 默认构造器，并没有在初始化时即以默认容量初始化数组
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
```

### add & 扩容函数

```java
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        // 可以为多个null
        elementData[size++] = e;
        return true;
    }
	// 在指定index插入
    public void add(int index, E element) {
        // 判断index是否越过当前边界
        rangeCheckForAdd(index);
		// 扩容判断
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        // 拷贝移动元素
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        elementData[index] = element;
        size++;
    }
    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }
	// 用于保障当使用默认构造器时，第一次新增时扩容容量最小为默认容量10；
	// 其他情况一般为size + nums.len
    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;
		// 最小所需容量超过当前容量时，扩容
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
	// 扩容函数，n = n + n/2
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        // 用于保证正常扩容；
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        // 容量边界处理
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // 拷贝旧数组元素到新数组
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
```

### remove

```java
    public E remove(int index) {
        // 下标判断
        rangeCheck(index);
		// 更新改动次数，并获取到旧值
        modCount++;
        E oldValue = elementData(index);
		// 将index + 1 至 size - 1 的元素复制到 index 至 size - 2
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        // 清空尾部数据
        elementData[--size] = null; // clear to let GC do its work
        return oldValue;
    }
	// 遍历删除元素
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }
    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }
```

### 缩容函数

```java
    public void trimToSize() {
        modCount++;
        if (size < elementData.length) {
            elementData = (size == 0)
              ? EMPTY_ELEMENTDATA
              : Arrays.copyOf(elementData, size);
        }
    }
```

### set & get

```java
    public E set(int index, E element) {
        rangeCheck(index);

        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }
	public E get(int index) {
        rangeCheck(index);

        return elementData(index);
    }
```

### contains

```java
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
    // 暴力遍历
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
```

## LinkedList

![LinkedList-UML](..\Resources\LinkedList-UML.png)

### 属性

```java
public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable{
    // 实际大小
    transient int size = 0;
    // 头节点与尾节点
    transient Node<E> first;
    transient Node<E> last;
}
```

### Constructor

```java
    public LinkedList() {
    }
	public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }
```

### 内部类 Node

```java
    //双向链表
	private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
```

### add

```java
    // 尾部插入
	public boolean add(E e) {
        linkLast(e);
        return true;
    }
	public void add(int index, E element) {
        // 越界判断
        checkPositionIndex(index);
		// 判断是尾部插入还是其他情况
        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }
	// 尾部插入集合
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }
    public boolean addAll(int index, Collection<? extends E> c) {
        // 越界判断
        checkPositionIndex(index);
		// 判断插入集合是否为空
        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0)
            return false;
		// 获取插入位置的前一个节点，和插入位置的当前节点
        Node<E> pred, succ;
        if (index == size) {
            succ = null;
            pred = last;
        } else {
            succ = node(index);
            pred = succ.prev;
        }
		// 遍历插入集合链接至插入位置后方
        for (Object o : a) {
            @SuppressWarnings("unchecked") E e = (E) o;
            Node<E> newNode = new Node<>(pred, e, null);
            if (pred == null)
                first = newNode;
            else
                pred.next = newNode;
            pred = newNode;
        }
		
        if (succ == null) {//是尾部插入，更新尾节点
            last = pred;
        } else {// 链接后半段
            pred.next = succ;
            succ.prev = pred;
        }
		
        size += numNew;
        modCount++;
        return true;
    }
    public void addFirst(E e) {
        linkFirst(e);
    }
    public void addLast(E e) {
        linkLast(e);
    }
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;后半部一定存在
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        // 前半部不一定存在，不存在则设置插入节点为头节点
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }
	// 头插法
    private void linkFirst(E e) {
        final Node<E> f = first;
        final Node<E> newNode = new Node<>(null, e, f);
        first = newNode;
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
        modCount++;
    }
	// 尾插法
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }
```

### remove

```java
    // 默认删除头节点，即队列先入先出
	public E remove() {
        return removeFirst();
    }
    public E remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }
	// 遍历删除链表中第一次出现的某个元素
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
    public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }
    public E removeLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }
	// 遍历删除链表中最后一次出现的某个元素
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
	// 删除一个存在的节点
    E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        modCount++;
        return element;
    }
	// 删除头节点
    private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }
	// 删除尾节点
    private E unlinkLast(Node<E> l) {
        // assert l == last && l != null;
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null; // help GC
        last = prev;
        if (prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        modCount++;
        return element;
    }
```

### set & get

```java
    // 判断下标，获取当前下标元素
	public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }
	// 获取头节点，不存在则报错
    public E getFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.item;
    }
    public E getLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return l.item;
    }
	// 通过下标判断是后序查找或前序查找
    Node<E> node(int index) {
        // assert isElementIndex(index);

        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }
    public E set(int index, E element) {
        checkElementIndex(index);
        Node<E> x = node(index);
        E oldVal = x.item;
        x.item = element;
        return oldVal;
    }
```

### contains

```java
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }
	// 暴力向后遍历
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null)
                    return index;
                index++;
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item))
                    return index;
                index++;
            }
        }
        return -1;
    }
```

### Deque 实现

- 获取元素：
    - peek() 、peekFirst() ：查询第一个元素，为空时不会报错；
    - peekLast()：查询最后一个元素，为空时不会报错；
- 插入元素：
    - *push(E e)、*offerFirst(E e)：与addFirst一样，实际上它就是addFirst；
    - *offer(E e)、offerLast(E e)*与addLast一样，实际上它就是addLast；
- 删除元素：
    - *pop()*：与removeFirst一样，实际上它就是removeFirst，为空时会报错；
    - *poll()*、pollFirst()：移除第一个元素，为空时不会报错；
    - *pollLast()*：移除最后一个元素，为空时不会报错；

## Vector

![Vector-UML](..\Resources\Vector-UML.png)

和ArrayList一模一样的结构

### 属性

```java
public class Vector<E>
    extends AbstractList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable{
    // 用来判断需要扩容多少
    protected int capacityIncrement;
    // 实际大小
    protected int elementCount;
    // 动态数组
    protected Object[] elementData;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
}
```

### Constructor

```java
	// Vector默认构造器在初始化时即初始化数组；默认容量也为10；
	public Vector() {
        this(10);
    }
    public Vector(Collection<? extends E> c) {
        elementData = c.toArray();
        elementCount = elementData.length;
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        if (elementData.getClass() != Object[].class)
            elementData = Arrays.copyOf(elementData, elementCount, Object[].class);
    }
	// 指定容量增值为0；
    public Vector(int initialCapacity) {
        this(initialCapacity, 0);
    }
	// 指定每次扩容大小
    public Vector(int initialCapacity, int capacityIncrement) {
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        this.elementData = new Object[initialCapacity];
        this.capacityIncrement = capacityIncrement;
    }
```

### add & 扩容函数

```java
// 和AaaryList逻辑一样只有扩容函数不一样，插入方式都以synchronized修饰
	public synchronized boolean add(E e) {
        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = e;
        return true;
    }
    private void ensureCapacityHelper(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
	// 
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        // 当增量为0时，采取n = 2n的方式，否则为 n = n + capIncrement(构造器指定的增量)
        int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
                                         capacityIncrement : oldCapacity);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
```

- 其他如*remove，get，set，indexOf，trimToSize*等方法均以*synchronized*修饰，逻辑与ArrayList相同

```java
	/* ArrayList不存在该方法，设置新的数组实际大小，如果大于当前实际大小，判断是否需要扩容，
	 * 中间部分初始化为null，如果新的实际大小小于当前实际大小，抹掉后面的数据
	 */ 
	public synchronized void setSize(int newSize) {
        modCount++;
        if (newSize > elementCount) {
            ensureCapacityHelper(newSize);
        } else {
            for (int i = newSize ; i < elementCount ; i++) {
                elementData[i] = null;
            }
        }
        elementCount = newSize;
    }
```

## List总结

**Array**

- Array 可以容纳基本类型和对象
- 容量大小固定

**ArrayList**

- 基于动态数组实现，实现了RandomAccess标识接口；
- 默认容量为10，在插入时才初始化数组；最大容量为 *Integer.MAX_VALUE* ；
- 扩容方式为 n = n + n/2;
- 查询速度快；
- 插入和删除时存在数组的拷贝，越接近头部越慢；效率较低；

**LinkedList**

- 基于双向队列（链表）实现，实现了Deque接口
- 查询速度较慢，越接近中心越慢；
- 插入和删除效率较高，但是插入和删除也存在寻址，所以只有接近头尾插时，效率才高；
- 使用 for 循环遍历效率特别低；

**Vector**

- 基于动态数组实现，实现了RandomAccess标识接口；
- 默认容量为10，初始化时即初始化数组；最大容量为 *Integer.MAX_VALUE* ；
- 扩容方式默认为 n = 2n; 否则为 n = n + capIncre(设置的正值)
- 其他逻辑与ArrayList相同，但主要对外方法均以 *synchronized* 修饰；即线程安全；
    - 但效率太低，且为遗留类，不建议使用，可以用*CopyOnWriteArrayList* 等类替代；

🦅 **适用场景分析**：

- 当需要对数据进行对随机访问的情况下，选用 ArrayList 。

- 当需要对数据进行多次增加删除修改时，采用 LinkedList 。

    > 如果容量固定，并且只会添加到尾部，不会引起扩容，优先采用 ArrayList 。

- 当然，绝大数业务的场景下，使用 ArrayList 就够了。主要是，注意好避免 ArrayList 的扩容，以及非顺序的插入。

# Set

![Set-UML](..\Resources\Set-UML.png)

- Set即集合，里面保存的是一堆不可重复的元素，使用equals方法来确保对象的唯一性。

## HashSet

![HashSet-UML](..\Resources\HashSet-UML.png)

### 属性

```java
    // 可以看出HashSet实际基于HashMap实现；
	private transient HashMap<E,Object> map;
    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();// 对象关联的虚值
```

### Constructor

```java
    public HashSet() {
        map = new HashMap<>();
    }
    public HashSet(Collection<? extends E> c) {
        map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
        addAll(c);
    }
    public HashSet(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }
    public HashSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }
	// dummy是一个被忽略的参数，只用来区分调用HashMap/LinkedHashMap的构造器；
	// 使用default修饰，实际是给予LinkedHashSet使用的；
    HashSet(int initialCapacity, float loadFactor, boolean dummy) {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }
```

### Method

大部分方法都直接基于HashMap的实现；

```java
    public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }
	// 该方法在AbstractCollection中
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c)
            if (add(e))
                modified = true;
        return modified;
    }
    public boolean remove(Object o) {
        return map.remove(o)==PRESENT;
    }
	// 该方法在AbstractSet中
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;

        if (size() > c.size()) {
            for (Iterator<?> i = c.iterator(); i.hasNext(); )
                modified |= remove(i.next());
        } else {
            for (Iterator<?> i = iterator(); i.hasNext(); ) {
                if (c.contains(i.next())) {
                    i.remove();
                    modified = true;
                }
            }
        }
        return modified;
    }
    public boolean contains(Object o) {
        return map.containsKey(o);
    }
	// 该方法在AbstractCollection中
    public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;
        return true;
    }
```

## LinkedHashSet

- 继承于HashSet，实现了调用生成LinkedHashMap的构造器;
- 内部结构基于LinkedHashMap;

### 源码

```java
public class LinkedHashSet<E>
    extends HashSet<E>
    implements Set<E>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = -2851667679971038690L;
    public LinkedHashSet() {
        super(16, .75f, true);
    }
    public LinkedHashSet(int initialCapacity) {
        super(initialCapacity, .75f, true);
    }
    public LinkedHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor, true);
    }
    public LinkedHashSet(Collection<? extends E> c) {
        super(Math.max(2*c.size(), 11), .75f, true);
        addAll(c);
    }

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.DISTINCT | Spliterator.ORDERED);
    }
}
```

## TreeSet

- 存储使用TreeMap；

```java
public class TreeSet<E> extends AbstractSet<E>
    implements NavigableSet<E>, Cloneable, java.io.Serializable{
    private transient NavigableMap<E,Object> m;// 实际基于NavigableMap的实现类

    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();// 对象关联的虚值
}
```

### Constructor

```java
	// 可以看出默认情况下基于TreeMap的实现
	public TreeSet() {
        this(new TreeMap<E,Object>());
    }
    public TreeSet(Collection<? extends E> c) {
        this();
        addAll(c);
    }
    public TreeSet(Comparator<? super E> comparator) {
        this(new TreeMap<>(comparator));
    }
    public TreeSet(SortedSet<E> s) {
        this(s.comparator());
        addAll(s);
    }
	// 使用default修饰；
    TreeSet(NavigableMap<E,Object> m) {
        this.m = m;
    }
```

## Set总结

主要Set实现类实际都直接使用对应的Map类作用存储结构，将存储的对象作为Map的键，使用一个*new Object()*作为键对应的值；

## 参考

[Programmer Help × JDK1.8 HashMap Source Code Analysis](https://programmer.help/blogs/jdk1.8-hashmap-source-code-analysis.html)

[简书 × TreeMap源码解析](https://www.jianshu.com/p/fc5e16b5c674)

[CSDN × LinkedHashMap源码解析（JDK8）](https://blog.csdn.net/zxt0601/article/details/77429150)