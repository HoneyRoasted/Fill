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
    public String qualified;

    @TestValueAnnotation("hello factory") public String testFactory;

    @Inject public boolean aBoolean;
    @Inject public boolean namedBoolean;

    public TestObject(@Inject String name) {
        this.name = name;
    }

    public void setDesc(@Inject String desc) {
        this.desc = desc;
    }

    public void qualified(@Inject("qualified") String qualified) {
        this.qualified = qualified;
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
                ", qualified='" + qualified + '\'' +
                ", testFactory='" + testFactory + '\'' +
                ", aBoolean=" + aBoolean +
                ", namedBoolean=" + namedBoolean +
                '}';
    }
}
