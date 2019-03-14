# spring-jvm #
JVM练习，熟悉启动项和简单的问题排错；

## IDEA 如何配置 ##
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190314163709208.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5X0xpZ2h0QnVsZQ==,size_16,color_FFFFFF,t_70)
脚本内容如下：
```shell
-server -Xmx3550m -Xms3550m -Xmn1256m -Xss128k -XX:SurvivorRatio=6 -XX:MaxPermSize=256m -XX:ParallelGCThreads=8 -XX:MaxTenuringThreshold=0 -XX:+UseConcMarkSweepGC
```
**解释这段配置：**
- -server
设置为server模式。
- -Xmx
设置最大堆内存为3350m
- -Xms
设置JVM初始堆内存为3550M。```此值可以设置与-Xmx相同，以避免每次垃圾回收完成后JVM重新分配内存。```
- -Xmn
设置年轻代大小为1256m。在整个堆内存大小确定的情况下，增大年轻代将会减小年老代，反之亦然。此值关系到JVM垃圾回收，对系统性能影响较大，官方推荐配置为整个堆大小的3/8。
- -Xss
设置较小的线程栈以支持创建更多的线程，支持海量访问，并提升系统性能。
- -XX:SurvivorRatio
设置年轻代中Eden区与Survivor区的比值。```系统默认是8，根据经验设置为6，则2个Survivor区与1个Eden区的比值为2:6，一个Survivor区占整个年轻代的1/8。```
- XX:MaxPermSize
设置持久代最大值为256M。
-  -XX:ParallelGCThreads
配置并行收集器的线程数。```即同时8个线程一起进行垃圾回收。此值一般配置为与CPU数目相等。```
- -XX:MaxTenuringThreshold
设置垃圾最大年龄（在年轻代的存活次数）。```如果设置为0的话，则年轻代对象不经过Survivor区直接进入年老代。对于年老代比较多的应用，可以提高效率；如果将此值设置为一个较大值，则年轻代对象会在Survivor区进行多次复制，这样可以增加对象再年轻代的存活时间，增加在年轻代即被回收的概率。根据被海量访问的动态Web应用之特点，其内存要么被缓存起来以减少直接访问DB，要么被快速回收以支持高并发海量请求，因此其内存对象在年轻代存活多次意义不大，可以直接进入年老代，根据实际应用效果，在这里设置此值为0。```
- XX:+UseConcMarkSweepGC
设置年老代为并发收集。```CMS（ConcMarkSweepGC）收集的目标是尽量减少应用的暂停时间，减少Full GC发生的几率，利用和应用程序线程并发的垃圾回收线程来标记清除年老代内存，适用于应用中存在比较多的长生命周期对象的情况。```

## 4个内存参数 ##
- **-Xmx**
Java Heap最大值，**```默认值为物理内存的1/4```**，最佳值应该视物理内存大小及计算机内其它内存开销而定；
- **-Xms**
Java Heap初始值，**```Server端JVM最好将-Xms和-Xmx设置相同```**，开发测试机JVM可以大一些。
- **-Xmn**
Java Heap Young区大小
- **-Xss**
每个线程的Stack大小

## 查看设置JVM内存信息 ##
```java
// 最大可用内存，对应-Xmx
Runtime.getRuntime().maxMemory();
// 当前JVM空闲内存
Runtime.getRuntime().freeMemory()
// 当前JVM占用的内存总数，其值相当于当前JVM已使用的内存及freeMemory()总合
Runtime.getRuntime().totalMemory()
```
- maxMemory （JVM最大可用内存）
可用过-Xmx设置，默认值为物理内存的1/4，值不能高于计算机物理内存；
- freeMemory （JVM当前空闲内存）
因为JVM只有在需要内存时才占用物理内存使用，所以，**freeMemory()的值一般情况下都很小**，而JVM实际可用内存并不等于freeMemory()，应该 **```等于maxMemonry()-totalMemory()+freeMemory()。```** 及其设置JVM内存分配。
- totalMemory （JVM当前所占用的内存总合）
值相当于当前JVM已使用的内存及freeMemory()的总合，会随着JVMl使用内存的增加而增加；

### 3个标准启动参数 ###
- 标准参数 “-”
输入的时候要输“-”，JVM实现都必须实现这些参数的功能，并且向后兼容；
例如下面代码段，**```“-”是必须要有的```**：
```shell
-server -XX:PermSize=196m -XX:MaxPermSize=196m -Xmn320m -Xms768m -Xmx1024m
```
- 非标准参数 “-X”
默认JVM实现这些参数的功能，但是并不保证所有JVM实现都满足，且保证向后兼容；
**执行“$ java -X”显示下面的信息**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190314140957442.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5X0xpZ2h0QnVsZQ==,size_16,color_FFFFFF,t_70)
- 非Stable参数 “-XX”
此参数不同版本的JVM实现会有所不同，**```未来可能随时取消，慎用；```**
#### 常用的标准参数 ####
- **-verbose:[class|gc|jni]**
**```启动详细输出，范例如下：```**
```shell
# 输出JVM载入类详细信息，当JVM报告说找不到类或者类冲突时可以用到。
java -verbose:class
# 输出每次GC的相关情况。
java -verbose:gc
# 输出native方法调用的相关情况，一般用于诊断jni调用错误信息。
java -verbose:jni
```

#### 非标准参数（扩展参数） ####
- -Xms512m
设置初始 Java 堆大小（内存），初始内存设置为512，至少要512M。
- -Xmx512m
设置最大 Java 堆大小（内存）。
- -Xmn200m
设置年轻代大小为200M，整个堆大小=年轻代大小 + 年老代大小 + 持久代大小。持久代一般固定大小为64m，所以增大年轻代后，将会减小年老代大小。此值对系统性能影响较大，Sun官方推荐配置为整个堆的3/8。
- -Xss128k
设置 Java 每个线程堆栈大小。**```减少这个参数能生成更多的线程，但是，操作系统对一个进程内的线程数是有上限的（取决于系统最大内存）。```**
- -Xloggc:flie
将 GC 状态记录在文件中 (带时间戳)，与-verbose:gc功能类似，只是 **```将每次GC事件的相关情况记录到一个文件中```**，文件的位置最好在本地，以避免网络的潜在问题。
 若与verbose命令同时出现在命令行中，则以-Xloggc为准。
- -Xprof
输出 cpu 配置文件数据，跟踪正运行的程序，并将跟踪数据在标准输出输出；适合于开发环境调试。

#### XX的使用 ####
-XX作为前缀的参数列表在jvm中可能是不健壮的，SUN也不推荐使用，后续可能会在没有通知的情况下就直接取消了；但是由于这些参数中的确有很多是对我们很有用的，比如我们经常会见到的-XX:PermSize、-XX:MaxPermSize等等；
- -XX:NewSize=1024m
设置年轻代初始值为1024M。
- -XX:MaxNewSize=1024m
设置年轻代最大值为1024M。
- -XX:PermSize=256m
设置持久代初始值为256M。
- -XX:MaxPermSize=256m
设置持久代最大值为256M。
- -XX:NewRatio=4
设置年轻代（包括1个Eden和2个Survivor区）与年老代的比值。表示年轻代比年老代为1:4。
- -XX:SurvivorRatio=4
设置**年轻代中Eden区与Survivor区的比值**。表示2个Survivor区（JVM堆内存年轻代中默认有2个大小相等的Survivor区）与1个Eden区的比值为2:4，即1个Survivor区占整个年轻代大小的1/6。
- -XX:MaxTenuringThreshold=7
表示一个对象如果在Survivor区（救助空间）移动了7次还没有被垃圾回收就进入年老代。如果设置为0的话，则年轻代对象不经过Survivor区，直接进入年老代，对于需要大量常驻内存的应用，这样做可以提高效率。如果将此值设置为一个较大值，则年轻代对象会在Survivor区进行多次复制，这样可以增加对象在年轻代存活时间，增加对象在年轻代被垃圾回收的概率，减少Full GC的频率，这样做可以在某种程度上提高服务稳定性。

### 启动参数的疑问 ###
- **-Xmn、-XX:NewSize / -XX:MaxNewSize、 -XX:NewRatio** ```三组参数都可以影响年轻代大小```，混合使用情况下，优先级是什么？
  + 高优先
-XX:NewSize/-XX:MaxNewSize
  + 中优先
-Xmn（默认等效  -Xmn=-XX:NewSize=-XX:MaxNewSize=?）
  + 低优先
-XX:NewRatio

```推荐使用-Xmn参数，原因是这个参数简洁，相当于一次设定 NewSize/MaxNewSIze，而且两者相等，适用于生产环境。-Xmn 配合 -Xms/-Xmx，即可将堆内存布局完成。```

```-Xmn参数是在JDK 1.4 开始支持。```

## 行为参数 ##
- -XX:-DisableExplicitGC
禁止调用System.gc()；但jvm的gc仍然有效
- -XX:+MaxFDLimit
最大化文件描述符的数量限制
- -XX:+ScavengeBeforeFullGC
新生代GC优先于Full GC执行
- -XX:+UseGCOverheadLimit
在抛出OOM之前限制jvm耗费在GC上的时间比例
- -XX:-UseConcMarkSweepGC（**并发收集器之一**）
**```对老生代采用并发标记交换算法进行GC```**
- -XX:-UseParallelGC（**并行收集器之一**）
**```启用并行GC```**
- -XX:-UseParallelOldGC
对Full GC启用并行，当-XX:-UseParallelGC启用时该项自动启用
- -XX:-UseSerialGC（**串行收集器**）
**```启用串行GC```**
- -XX:+UseThreadPriorities
启用本地线程优先级
### 关于垃圾收集器 ###
即 **```垃圾回收器```**，JVM给出3种选择：串行收集器、并行收集器、并发收集器。

#### 串行收集器 ####
是jvm的默认GC方式，一般适用于小型应用和单处理器，算法比较简单，GC效率也较高，但可能会给应用带来停顿；
- -XX:+UseSerialGC
设置串行收集器
#### 并行收集器（吞吐量优先） ####
是指GC运行时，对应用程序运行没有影响，GC和app两者的线程在并发执行，这样可以最大限度不影响app的运行；
- -XX:+UseParallelGC
设置为并行收集器。此配置仅对年轻代有效。即 **```年轻代使用并行收集，而年老代仍使用串行收集。```**
- -XX:ParallelGCThreads=20
配置并行收集器的线程数，即：**```同时有多少个线程一起进行垃圾回收```**。此值建议配置与CPU数目相等。
- -XX:+UseParallelOldGC
配置年老代垃圾收集方式为并行收集。JDK6.0开始支持对年老代并行收集。
- -XX:MaxGCPauseMillis=100
**```设置每次年轻代垃圾回收的最长时间（单位毫秒）```**。如果无法满足此时间，JVM会自动调整年轻代大小，以满足此时间。
- -XX:+UseAdaptiveSizePolicy
设置此选项后，并行收集器会自动调整年轻代Eden区大小和Survivor区大小的比例，以达成目标系统规定的最低响应时间或者收集频率等指标。此参数建议在使用并行收集器时，一直打开。
#### 并发收集器（响应时间优先） ####
是指多个线程并发执行GC，一般适用于多处理器系统中，可以提高GC的效率，但算法复杂，系统消耗较大；
- -XX:+UseConcMarkSweepGC
**```即CMS收集，设置年老代为并发收集。```**
CMS收集是JDK1.4后期版本开始引入的新GC算法。它的主要适合场景是对响应时间的重要性需求大于对吞吐量的需求，能够承受垃圾回收线程和应用线程共享CPU资源，并且应用中存在比较多的长生命周期对象。CMS收集的目标是尽量减少应用的暂停时间，减少Full GC发生的几率，利用和应用程序线程并发的垃圾回收线程来标记清除年老代内存。
- -XX:+UseParNewGC
**```设置年轻代为并发收集```**。
可与CMS收集同时使用。JDK5.0以上，JVM会根据系统配置自行设置，所以无需再设置此参数。
- -XX:CMSFullGCsBeforeCompaction=0
由于并发收集器不对内存空间进行压缩和整理，所以运行一段时间并行收集以后会产生内存碎片，内存使用效率降低。此参数设置运行0次Full GC后对内存空间进行压缩和整理，即每次Full GC后立刻开始压缩和整理内存。
- -XX:+UseCMSCompactAtFullCollection
打开内存空间的压缩和整理，在Full GC后执行。可能会影响性能，但可以消除内存碎片。
- -XX:+CMSIncrementalMode
设置为增量收集模式。一般适用于单CPU情况。
- -XX:CMSInitiatingOccupancyFraction=70
表示年老代内存空间使用到70%时就开始执行CMS收集，以确保年老代有足够的空间接纳来自年轻代的对象，避免Full GC的发生。

## 性能调优参数 ##
- -XX:LargePageSizeInBytes=4m
设置用于Java堆的大页面尺寸
- -XX:MaxHeapFreeRatio=70
GC后java堆中空闲量占的最大比例
- **-XX:MaxNewSize=size**
新生成对象能占用内存的最大值
- **-XX:MaxPermSize=64m**
老生代对象能占用内存的最大值
- -XX:MinHeapFreeRatio=40
GC后java堆中空闲量占的最小比例
- -XX:NewRatio=2
新生代内存容量与老生代内存容量的比例
- **-XX:NewSize=2.125m**
新生代对象生成时占用内存的默认值
- -XX:ReservedCodeCacheSize=32m
保留代码占用的内存容量
- -XX:ThreadStackSize=512
设置线程栈大小，若为0则使用系统默认值
- -XX:+UseLargePages
使用大页面内存

```黑体加粗在实际工作中是经常用来调试的```

## 调试参数 ##
- -XX:-CITime
打印消耗在JIT编译的时间
- -XX:ErrorFile=./hs_err_pid<pid>.log
保存错误日志或者数据到文件中
- -XX:-ExtendedDTraceProbes
开启solaris特有的dtrace探针
- **```-XX:HeapDumpPath=./java_pid<pid>.hprof```**
**指定导出堆信息时的路径或文件名**
- **```-XX:-HeapDumpOnOutOfMemoryError```**
**当首次遭遇OOM时导出此时堆中相关信息**
- -XX:OnOutOfMemoryError="<cmd args>;<cmd args>"
当首次遭遇OOM时执行自定义命令
- -XX:-PrintClassHistogram
遇到Ctrl-Break后打印类实例的柱状信息，与jmap -histo功能相同
- **```-XX:-PrintConcurrentLocks```**
**遇到Ctrl-Break后打印并发锁的相关信息，与jstack -l功能相同**
- -XX:-PrintCommandLineFlags
打印在命令行中出现过的标记
- -XX:-PrintCompilation
当一个方法被编译时打印相关信息
- -XX:-PrintGC
每次GC时打印相关信息
- -XX:-PrintGC Details
每次GC时打印详细信息
- -XX:-PrintGCTimeStamps
打印每次GC的时间戳
- -XX:-TraceClassLoading
跟踪类的加载信息
- -XX:-TraceClassLoadingPreorder
跟踪被引用到的所有类的加载信息
- -XX:-TraceClassResolution
跟踪常量池
- -XX:-TraceClassUnloading
跟踪类的卸载信息
- -XX:-TraceLoaderConstraints
跟踪类加载器约束的相关信息
