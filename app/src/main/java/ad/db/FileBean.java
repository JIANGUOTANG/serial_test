package ad.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ying on 17-11-1.
 */
@Entity
public class FileBean {

    /**
     * Copyright 2017 bejson.com
     */
    @Unique
    @Id
    private Long id;
    private String name;
    @Unique
    private String url;
    private int time;
    private int machineID;//灯光的ID
    private int userID;
    private String remark;
    @Generated(hash = 699019529)
    public FileBean(Long id, String name, String url, int time, int machineID,
            int userID, String remark) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.time = time;
        this.machineID = machineID;
        this.userID = userID;
        this.remark = remark;
    }
    @Generated(hash = 1910776192)
    public FileBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getTime() {
        return this.time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public int getMachineID() {
        return this.machineID;
    }
    public void setMachineID(int machineID) {
        this.machineID = machineID;
    }
    public int getUserID() {
        return this.userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
   
}
