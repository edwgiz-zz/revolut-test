package ru.edwgiz.test.rest.framework.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;


public final class BigDecimalAdapter extends XmlAdapter<String, BigDecimal> {

    @Override
    public BigDecimal unmarshal(String v) throws Exception {
        return new BigDecimal(v);
    }

    @Override
    public String marshal(BigDecimal v) throws Exception {
        return v.toPlainString();
    }
}
