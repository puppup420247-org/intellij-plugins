package com.intellij.javascript.flex.mxml.schema;

import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptorAwareAboutChildren;
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;

class AnyXmlElementWithAnyChildrenDescriptor extends AnyXmlElementDescriptor implements XmlElementDescriptorAwareAboutChildren {

  AnyXmlElementWithAnyChildrenDescriptor() {
    super(null, null);
  }

  @Override
  public boolean allowElementsFromNamespace(final String namespace, final XmlTag context) {
    return false;
  }
}
