package com.cy.jvm.config;

import com.cy.jvm.bean.VmVO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @description：
 * @author: Yang.Chang
 * @project: spring-jvm
 * @package: com.cy.jvm.config、
 * @email: cy880708@163.com
 * @date: 2019/3/14 下午4:22
 * @mofified By:
 */
public class StartConfig {

    /**
     * @description：定义一个内存溢出函数，并执行把报告导出至系统根目录，查看报告分析内存溢出原因，目的是内存溢出时如何排错
     * 1. 先修改启动项
     * -server -Xmx64m -Xms64m -Xmn32m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/WORK/Project_Java/spring-jvm
     * 2. 运行
     * 3. 控制台
     * Dumping heap to /WORK/Project_Java/spring-jvm/java_pid81744.hprof ...
     * 4. 查看java_pid81744.hprof
     * @version 1.0
     * @author: Yang.Chang
     * @email: cy880708@163.com
     * @date: 2019/3/14 下午4:23
     * @mofified By:
     */
    public static VmVO outOf(int i) {
        VmVO vmVO = new VmVO();
        vmVO.setVmId(UUID.randomUUID().toString().replaceAll("-", ""));
        vmVO.setVmCode(i+"");
        vmVO.setVmName("frank_" + i);
        return vmVO;
    }

    public static void main(String[] args) {
        List arrayList = new ArrayList();
        for(int i=0; true; i++) {
            arrayList.add(outOf(i));
        }
    }
}
