package org.abcd.sysmlevolver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

import org.abcd.sysmlevolver.test.log.PerformanceLogger;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.papyrus.moka.debug.MokaDebugTarget;
import org.eclipse.papyrus.moka.engine.MokaExecutionEngineJob;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.osgi.framework.Bundle;

public class Application implements IApplication {

	private Model model = null;
	private EObject eObjectToExecute = null;

	private String caseName = null;
	private String instanceArg = "-i ";

	public Object start(IApplicationContext context) throws Exception {
		executeTestModel();

		return IApplication.EXIT_OK;
		

	}


	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}

	private void executeTestModel() throws CoreException, IOException {
		ExecutionEngine engine = new ExecutionEngine();

		String[] args = { instanceArg };
		int requestPort = -1;
		int replyPort = -1;
		int eventPort = -1;
		requestPort = findFreePort();
		eventPort = findFreePort();
		replyPort = findFreePort();
		MokaDebugTarget target = new MokaDebugTarget(null, null);
		engine.init(eObjectToExecute, args, target, requestPort, replyPort, eventPort);
		engine.start();


	}

	protected static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) {
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		return -1;
	}
}
