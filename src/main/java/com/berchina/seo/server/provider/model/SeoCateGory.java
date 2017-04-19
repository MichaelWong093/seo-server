package com.berchina.seo.server.provider.model;

import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @Package com.berchina.seo.server.provider.model
 * @Description: TODO ( 类目实体类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/9 下午3:59
 * @Version V1.0
 */
public class SeoCateGory implements Serializable {

    private static final long serialVersionUID = 7648758068108775837L;

    /**
     * 父级编号
     */
    private String id;

    /**
     * 类目编号
     */
    private String key;

    /**
     * 类目名称
     */
    private String value;

    /**
     * 叶子类目
     */
    private LinkedList<SeoCateGory> childs = Lists.newLinkedList();


    public SeoCateGory() {
    }

    public SeoCateGory(String id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public SeoCateGory(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LinkedList<SeoCateGory> getChilds() {
        if (!StringUtils.isEmpty(childs) && childs.size() > 0) {
            return childs;
        }
        return null;
    }

    public void setChilds(LinkedList<SeoCateGory> childs) {
        if (!StringUtils.isEmpty(childs) && childs.size() > 0) {
            this.childs = childs;
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj instanceof SeoCateGory) {
            SeoCateGory sku = (SeoCateGory) obj;
//          if(user.id = this.id) return true; // 只比较id
            // 比较id和username 一致时才返回true 之后再去比较 hashCode
            if (StringUtil.notNull(sku.getKey()) && StringUtil.notNull(sku.getValue())){
                if (Objects.equals(sku.getKey(), this.key)
                        && sku.getValue().equals(this.value)) return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (StringUtil.notNull(key) && StringUtil.notNull(value)) {
            return key.hashCode() * value.hashCode();
        }
        return 0;
    }

    public static class Brand {

        private String id;

        private String name;

        private String bdLogo;

        public Brand() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBdLogo() {
            return bdLogo;
        }

        public void setBdLogo(String bdLogo) {
            this.bdLogo = bdLogo;
        }

        @Override
        public String toString() {
            return "Brand{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", bdLogo='" + bdLogo + '\'' +
                    '}';
        }
    }
}
