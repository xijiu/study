package com.lkn.dag.handlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.sql.Timestamp;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author rock
 */
public class DeployTask {
    public int seq;
    public String taskId;
    public String instanceId;
    public int taskLevel;
    public String action;
    public String targetId;
    public int status = 0;
    public long exec = -1L;
    public String data;
    public Timestamp createTime;
    public Timestamp modifyTime;
    public String modifyBy;

    public DeployTask() {
    }

    public DeployTask(String action, int taskLevel, String targetId) {
        this.action = action;
        this.taskLevel = taskLevel;
        this.targetId = targetId;
    }

    public DeployTask(String action, int taskLevel, String targetId, String data) {
        this(action, taskLevel, targetId);
        this.data = data;
    }

    public DeployTask(String action, int taskLevel, String targetId, String data, long exec) {
        this(action, taskLevel, targetId, data);
        this.exec = exec;
        if (this.exec > 0) {
            this.status = 3;
        }
    }

    public DeployTask(int seq, String taskId, String instanceId, int taskLevel, String action, String targetId, String data) {
        this.seq = seq;
        this.taskId = taskId;
        this.instanceId = instanceId;
        this.taskLevel = taskLevel;
        this.action = action;
        this.targetId = targetId;
        this.data = data;
    }

    public DeployTask(String taskId, String instanceId, int taskLevel, String action, String targetId, String data) {
        this.taskId = taskId;
        this.instanceId = instanceId;
        this.taskLevel = taskLevel;
        this.action = action;
        this.targetId = targetId;
        this.data = data;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public int getTaskLevel() {
        return taskLevel;
    }

    public void setTaskLevel(int taskLevel) {
        this.taskLevel = taskLevel;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getExec() {
        return exec;
    }

    public void setExec(long exec) {
        this.exec = exec;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public Map<String, String> findDataAsMap() {
        return JSON.parseObject(data, new TypeReference<TreeMap<String, String>>() {
        });
    }

}
