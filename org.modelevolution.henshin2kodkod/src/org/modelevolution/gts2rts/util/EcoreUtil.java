/* 
 * henshin2kodkod -- Copyright (c) 2014-present, Sebastian Gabmeyer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.modelevolution.gts2rts.util;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EcorePackage.Literals;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class EcoreUtil {

  public static boolean isEEnum(final EClassifier type) {
    if (type == null)
      throw new NullPointerException();
    return type instanceof EEnum;
  }

  public static boolean isEBoolean(final EClassifier type) {
    if (type == null)
      throw new NullPointerException();
    return type == EcorePackage.Literals.EBOOLEAN;
  }

  public static boolean isEInt(final EClassifier type) {
    if (type == null)
      throw new NullPointerException();
    return type == EcorePackage.Literals.EINT;
  }

  public static boolean isEEnum(final EStructuralFeature feature) {
    if (feature == null)
      throw new NullPointerException();
    return feature.getEType() instanceof EEnum; // ==
                                                // EcorePackage.Literals.EENUM;
  }

  public static boolean isEBoolean(final EAttribute a) {
    if (a == null)
      throw new NullPointerException();
    return a.getEAttributeType() == EcorePackage.Literals.EBOOLEAN;
  }

  public static boolean isEInt(final EAttribute a) {
    if (a == null)
      throw new NullPointerException();
    return a.getEAttributeType() == EcorePackage.Literals.EINT;
  }

  public static Collection<EClass> getEClasses(final EPackage ePackage) {
    final Collection<EClass> classes = new ArrayList<>();
    for (EClassifier c : ePackage.getEClassifiers()) {
      if (c instanceof EClass)
        classes.add((EClass) c);
    }
    return classes;
  }

}
