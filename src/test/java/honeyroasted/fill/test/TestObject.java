package honeyroasted.fill.test;

import honeyroasted.fill.Inject;

import java.util.List;

public class TestObject {
    @Inject public int x;
    @Inject public String z;

    @Inject public List<String> listStr;
    @Inject public List<Integer> listInt;
    @Inject public List<? extends Object> listWildObj;

    @TestAnnotation public String k;

    public String name;
    public String desc;

    @Inject public boolean aBoolean;
    @Inject public boolean namedBoolean;

    public TestObject(@Inject String name) {
        this.name = name;
    }

    @Inject
    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "x=" + x +
                ", z='" + z + '\'' +
                ", listStr=" + listStr +
                ", listInt=" + listInt +
                ", listWildObj=" + listWildObj +
                ", k='" + k + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", aBoolean=" + aBoolean +
                ", namedBoolean=" + namedBoolean +
                '}';
    }
}
