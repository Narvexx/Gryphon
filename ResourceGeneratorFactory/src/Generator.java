import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class Generator 
{
	public static void main(String args[]) {
		
		final int PhilCount = 5;
		
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		URI fileURI = URI.createFileURI("D:\\Users\\mitch\\repos\\Gryphon\\ResourceGeneratorFactory\\model\\diningphils.ecore");
		Resource res = resourceSet.getResource(fileURI, true);	
		EPackage metapackage = (EPackage) res.getContents().get(0);
		EFactory tableFactoryInstance = metapackage.getEFactoryInstance();
		
		// Create Table object
	    EClass tableClass = (EClass)metapackage.getEClassifier("Table");
	    EObject tableObject = tableFactoryInstance.create(tableClass);
	    
	    EStructuralFeature philosopherFeature = tableClass.getEStructuralFeature("philosophers");
	    @SuppressWarnings("unchecked")
	    List<EObject> philosophers = (List<EObject>) tableObject.eGet(philosopherFeature);
	    
	    EStructuralFeature forkFeature = tableClass.getEStructuralFeature("forks");
	    @SuppressWarnings("unchecked")
	    List<EObject> forks = (List<EObject>) tableObject.eGet(forkFeature);
	    
	    // Create Fork & Philosopher objects
	    EClass forkClass = (EClass)metapackage.getEClassifier("Fork");
	    EClass philClass = (EClass)metapackage.getEClassifier("Philosopher");
	    
	    for (int i = 0; i < PhilCount; i++) {
	    	EObject forkObject = tableFactoryInstance.create(forkClass);
		    EAttribute forkId = forkClass.getEAllAttributes().get(0);
		    forkObject.eSet(forkId, i);
		    
		    forks.add(forkObject);
	    }
	    
	    for (int i = 0; i < PhilCount; i++) {
	    	EObject philObject = tableFactoryInstance.create(philClass);
	    	
	    	EAttribute philId = philClass.getEAttributes().get(0);
	    	philObject.eSet(philId, i);
	    	
		    EStructuralFeature left = philClass.getEStructuralFeature("left");
		    philObject.eSet(left, forks.get(i));
		    
		    EStructuralFeature right = philClass.getEStructuralFeature("right");
		    if (i + 1 >= PhilCount) {
		    	philObject.eSet(right, forks.get(0));
		    } else {
		    	philObject.eSet(right, forks.get(i + 1));
		    }
		    
		    philosophers.add(philObject);
	    }
	    
	    // Create XMI instance
	    ResourceSet resourseSet = new ResourceSetImpl();
	    resourseSet.getPackageRegistry().put(metapackage.getNsURI(), metapackage);
	    resourseSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
	    Resource resource = resourseSet.createResource(URI.createURI("./model/" + PhilCount + "-philsInit.xmi"));
	    resource.getContents().add(tableObject);
	    Map<String, Boolean> options = new HashMap<String, Boolean>();
	    options.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
		try
		{
		resource.save(options);
		}
		catch (IOException e) {}
	}
}