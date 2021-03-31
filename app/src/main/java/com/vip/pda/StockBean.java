package com.vip.pda;

import java.io.Serializable;
import java.util.List;

public class StockBean implements Serializable {
    private String dh;
    private List<String> list;

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
