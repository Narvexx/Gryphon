/* 
 * henshin2kodkod -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class HenshinLoader {
  private Module module;

  public HenshinLoader(String workdir, String henshinFile) {
    if (workdir == null || henshinFile == null)
      throw new NullPointerException();
    if (workdir.isEmpty() || henshinFile.isEmpty())
      throw new IllegalArgumentException();

    final HenshinResourceSet resourceSet = new HenshinResourceSet(workdir);
    module = resourceSet.getModule(henshinFile);
    if (module == null)
      throw new HenshinLoaderException("No module could be loaded from " + henshinFile);
  }

  /**
   * @return
   */
  public List<Rule> getTransformations() {
    EList<Unit> units = module.getUnits();
    List<Rule> rules = new ArrayList<>(units.size());
    for (Unit u : units) {
      if (u instanceof Rule && u.isActivated())
        rules.add((Rule) u);
    }
    return rules;
  }

  public EList<EPackage> getMetamodels() {
    return module.getImports();
  }

  public Module getModule() {
    return module;
  }

  public String moduleName() {
    return filename(module.eResource().getURI().lastSegment());
  }

  private String filename(final String file) {
    final int dotPos = file.lastIndexOf('.');
    if (dotPos < 0)
      return file;
    else
      return file.substring(0, dotPos);
  }

  /**
   * Get the first metamodel from the list of imported metamodels in the Henshin
   * module.
   * 
   * @return
   */
  public EPackage getMetamodel() {
    if (module.getImports() == null || module.getImports().isEmpty())
      throw new HenshinLoaderException("No metamodel could be loaded from the module "
          + " because there are none.");
    return module.getImports().get(0);
  }
}
