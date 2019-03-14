package com.cy.jvm.bean;

import java.io.Serializable;

/**
 * @version 1.0
 * @description：
 * @author: Yang.Chang
 * @project: spring-jvm
 * @package: com.cy.jvm.bean、
 * @email: cy880708@163.com
 * @date: 2019/3/14 下午4:53
 * @mofified By:
 */
public class VmVO implements Serializable {

    private static final long serialVersionUID = 7662305686639833768L;

    private String vmId;

    private String vmCode;

    private String vmName;

    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public String getVmCode() {
        return vmCode;
    }

    public void setVmCode(String vmCode) {
        this.vmCode = vmCode;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public VmVO() {
    }

    public VmVO(String vmId, String vmCode, String vmName) {
        this.vmId = vmId;
        this.vmCode = vmCode;
        this.vmName = vmName;
    }
}
