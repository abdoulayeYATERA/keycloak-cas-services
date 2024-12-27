package io.github.johnjcool.keycloak.broker.cas.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Function {
  private String mUAI;
  private String mFunction;

  @XmlElement(name = "UAI")
  public String getmUAI() {
    return mUAI;
  }

  public void setmUAI(String mUAI) {
    this.mUAI = mUAI;
  }
 @XmlElement(name = "function")
 public String getmFunction() {
    return mFunction;
  }

  public void setmFunction(String mFunction) {
    this.mFunction = mFunction;
  }
}

