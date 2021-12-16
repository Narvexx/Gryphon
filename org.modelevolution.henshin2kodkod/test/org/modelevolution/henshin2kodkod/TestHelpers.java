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
package org.modelevolution.henshin2kodkod;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.modelevolution.emf2rel.FeatureMerger;

/**
 * @author Sebastian Gabmeyer
 *
 */
public abstract class TestHelpers {
  /**
   * @param pkg
   * @param instancepath
   * @return
   * @throws IOException
   */
  public static Resource loadInstance(EPackage pkg, String instancepath) throws IOException {
    final ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getPackageRegistry().put(pkg.getNsURI(), pkg);
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put("xmi", new XMIResourceFactoryImpl());
    final Resource res = resourceSet.createResource(URI.createFileURI(instancepath));
    res.load(null);
    return res;
  }
  
  /**
   * @param path
   * @return
   */
  public static EPackage loadModel(final String path) {
    final ResourceSet resourceSet = new ResourceSetImpl();

    // Register the default resource factory -- only needed for stand-alone!
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put("ecore", new EcoreResourceFactoryImpl());

    // Register the package -- only needed for stand-alone!
    // final EcorePackage ecorePackage = EcorePackage.eINSTANCE;

    // Get the URI of the model file.
    final URI fileURI = URI.createFileURI(new File(path).getAbsolutePath());

    // Demand load the resource for this file.
    final Resource resource = resourceSet.getResource(fileURI, true);
    return (EPackage) resource.getContents().get(0);
  }
  
  /**
   * @param model
   * @return
   */
  public static FeatureMerger getPacmanMerger(final EPackage model) {
    final EReference pacman_on = (EReference)((EClass)model.getEClassifier("Pacman")).getEStructuralFeature("on");
    final EReference ghost_on = (EReference)((EClass)model.getEClassifier("Ghost")).getEStructuralFeature("on");
    final FeatureMerger fm = FeatureMerger.create("on", pacman_on, ghost_on);
    return fm;
  }
}
