package io.github.johnjcool.keycloak.broker.cas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ENTFunction {

  @XmlElement(name = "UAI", namespace = "http://www.yale.edu/tp/cas")
  @JsonProperty("UAI")
  private String mUAI;

  @XmlElement(name = "function", namespace = "http://www.yale.edu/tp/cas")
  @JsonProperty("function")
  private String mFunction;

  public String getmUAI() {
    return mUAI;
  }

  public void setmUAI(String mUAI) {
    this.mUAI = mUAI;
  }

 public String getmFunction() {
    return mFunction;
  }

  public void setmFunction(String mFunction) {
    this.mFunction = mFunction;
  }
}

