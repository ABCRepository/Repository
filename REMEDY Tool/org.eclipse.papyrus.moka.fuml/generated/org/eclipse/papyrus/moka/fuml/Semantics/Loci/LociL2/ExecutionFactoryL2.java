/*****************************************************************************
 * Copyright (c) 2012 CEA LIST.
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL2;

import org.eclipse.papyrus.moka.fuml.Semantics.Actions.BasicActions.CallBehaviorActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.BasicActions.CallOperationActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.BasicActions.InputPinActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.BasicActions.OutputPinActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.BasicActions.SendSignalActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.AddStructuralFeatureValueActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.ClearAssociationActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.ClearStructuralFeatureActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.CreateLinkActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.CreateObjectActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.DestroyLinkActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.DestroyObjectActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.ReadLinkActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.ReadSelfActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.ReadStructuralFeatureActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.RemoveStructuralFeatureValueActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.TestIdentityActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.ValueSpecificationActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.ActivityExecution;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.ActivityFinalNodeActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.ActivityParameterNodeActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.DecisionNodeActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.FlowFinalNodeActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.ForkNodeActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.InitialNodeActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.JoinNodeActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.MergeNodeActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.ExecutionFactoryL1;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.SemanticVisitor;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityFinalNode;
import org.eclipse.uml2.uml.ActivityParameterNode;
import org.eclipse.uml2.uml.AddStructuralFeatureValueAction;
import org.eclipse.uml2.uml.CallBehaviorAction;
import org.eclipse.uml2.uml.CallOperationAction;
import org.eclipse.uml2.uml.ClearAssociationAction;
import org.eclipse.uml2.uml.ClearStructuralFeatureAction;
import org.eclipse.uml2.uml.CreateLinkAction;
import org.eclipse.uml2.uml.CreateObjectAction;
import org.eclipse.uml2.uml.DecisionNode;
import org.eclipse.uml2.uml.DestroyLinkAction;
import org.eclipse.uml2.uml.DestroyObjectAction;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.FlowFinalNode;
import org.eclipse.uml2.uml.ForkNode;
import org.eclipse.uml2.uml.InitialNode;
import org.eclipse.uml2.uml.InputPin;
import org.eclipse.uml2.uml.JoinNode;
import org.eclipse.uml2.uml.MergeNode;
import org.eclipse.uml2.uml.OutputPin;
import org.eclipse.uml2.uml.ReadLinkAction;
import org.eclipse.uml2.uml.ReadSelfAction;
import org.eclipse.uml2.uml.ReadStructuralFeatureAction;
import org.eclipse.uml2.uml.RemoveStructuralFeatureValueAction;
import org.eclipse.uml2.uml.SendSignalAction;
import org.eclipse.uml2.uml.TestIdentityAction;
import org.eclipse.uml2.uml.ValueSpecificationAction;

public class ExecutionFactoryL2 extends ExecutionFactoryL1 {

	public ExecutionFactoryL2() {
		super();
	}

	@Override
	public SemanticVisitor instantiateVisitor(Element element) {
		// Instantiate a visitor object for the given element (at Conformance
		// Level 2)
		SemanticVisitor visitor = null;
		if (element instanceof Activity) {
			visitor = new ActivityExecution();
		} else if (element instanceof ActivityParameterNode) {
			visitor = new ActivityParameterNodeActivation();
		} else if (element instanceof InitialNode) {
			visitor = new InitialNodeActivation();
		} else if (element instanceof ActivityFinalNode) {
			visitor = new ActivityFinalNodeActivation();
		} else if (element instanceof FlowFinalNode) {
			visitor = new FlowFinalNodeActivation();
		} else if (element instanceof JoinNode) {
			visitor = new JoinNodeActivation();
		} else if (element instanceof MergeNode) {
			visitor = new MergeNodeActivation();
		} else if (element instanceof ForkNode) {
			visitor = new ForkNodeActivation();
		} else if (element instanceof DecisionNode) {
			visitor = new DecisionNodeActivation();
		} else if (element instanceof InputPin) {
			visitor = new InputPinActivation();
		} else if (element instanceof OutputPin) {
			visitor = new OutputPinActivation();
		} else if (element instanceof CallBehaviorAction) {
			visitor = new CallBehaviorActionActivation();
		} else if (element instanceof CallOperationAction) {
			visitor = new CallOperationActionActivation();
		} else if (element instanceof SendSignalAction) {
			visitor = new SendSignalActionActivation();
		} else if (element instanceof ReadSelfAction) {
			visitor = new ReadSelfActionActivation();
		} else if (element instanceof TestIdentityAction) {
			visitor = new TestIdentityActionActivation();
		} else if (element instanceof ValueSpecificationAction) {
			visitor = new ValueSpecificationActionActivation();
		} else if (element instanceof CreateObjectAction) {
			visitor = new CreateObjectActionActivation();
		} else if (element instanceof DestroyObjectAction) {
			visitor = new DestroyObjectActionActivation();
		} else if (element instanceof ReadStructuralFeatureAction) {
			visitor = new ReadStructuralFeatureActionActivation();
		} else if (element instanceof ClearStructuralFeatureAction) {
			visitor = new ClearStructuralFeatureActionActivation();
		} else if (element instanceof AddStructuralFeatureValueAction) {
			visitor = new AddStructuralFeatureValueActionActivation();
		} else if (element instanceof RemoveStructuralFeatureValueAction) {
			visitor = new RemoveStructuralFeatureValueActionActivation();
		} else if (element instanceof ReadLinkAction) {
			visitor = new ReadLinkActionActivation();
		} else if (element instanceof ClearAssociationAction) {
			visitor = new ClearAssociationActionActivation();
		} else if (element instanceof CreateLinkAction) {
			visitor = new CreateLinkActionActivation();
		} else if (element instanceof DestroyLinkAction) {
			visitor = new DestroyLinkActionActivation();
		} else {
			visitor = super.instantiateVisitor(element);
		}
		return visitor;
	}
}
