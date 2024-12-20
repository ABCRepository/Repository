# Safety Behaviour Abstraction and Model Evolution in Autonomous Driving

This repository contains:

**1. REMEDY Tool** -  The REMEDY Tool, which includes ETM-Executor and ETM-Evolver for enabling the execution and evolution of the ETMs;

**2. MEDY Profile** - The MEDY Profile defines three profiles to distinguish three types of variables, two types of operations, and three behaviours;

**3. Running Example** - The Running example with a SysML model, including a block definition diagram and a state machine, to capture the behaviour of driving through a block;

**4. Data Sets** - All the raw data for two experiments.

## Contributions ##
**1.** propose a novel approach (REMEDY) for automatically discovering previously unknown ADS behaviours and specifying them as executable state machines constructed with MEDY.

**2.** propose a solution to evolve state machines via model execution and simulation.

**3.** Implement risk-based strategy RiOT using Q-learning to drive model execution, model evolution, and simulation.

**4.** conduct an empirical evaluation to assess the cost-effectiveness of the strategy by comparing it with three comparision baselines, i.e., random strategy
(RT), a coverage-based greedy strategy (COT), and a start-of-art RL-based approach DeepCollision.  Results show that RiOT outperforms RT and COT in dis-
covering risky ADS behaviours, while COT performs the best in exploring more diverse behaviours. Regarding behavious diversity, COT achieved the
best performance, RiOT performed the worst, and REMEDY outperforms DeepCollision.

**5.** implement REMEDY with the three strategies (i.e., RiOT, COT, and RT) that support model execution and evolution as a prototype.

## Overview of REMEDY ##
An overview of REMEDY is shown here. REMEDY aims to execute executable test models (in SysML state machines) deployed on SysML-based modelling and model execution environment Papyrus and
Moka, and the virtual vehicle deployed on a simulator (in our case, CARLA) to evolve test models for discovering previously-unknown behaviours. REMEDY is equiped with two components (i.e., ETM-Executor
and ETM-Evolver). ETM-Executor is used to execute ETM and ETM-Evolver is used to evolve ETM.
![image](https://github.com/ABCRepository/Repository/blob/main/Overview%20of%20REMEDY/overview%20of%20framework1.png)

## REMEDY Environment Configuration ##
**1. Construct initial ETM model**

To construct an ETM (i.e., executable test model) using a SysML model, begin by building a block definition diagram and a state machine diagram, and then apply defined MEDY profiles to the ETM based on [Papyrus](https://projects.eclipse.org/projects/modeling.mdt.papyrus)

**2. Configure CARLA setting**

Using APIs for getting corresponding attributes of the virtual vehicle and environment (e.g., speed), controlling the execution of the virtual vehicle (e.g. turnLeft), and configuring the dynamic driving environment (e.g., adding NPC vehicles around the virtual vehicle and setting clear weather), the detailed instructions please look at [CARLA](https://carla.readthedocs.io/en/latest/python_api).

**3. Explore ADS behaviors with Q-Learning**
Using Q-Learning to explore ADS behaviors with high risk, this study primarily defines risky behaviors with a focus on collisions and potential collisions, including violations of safety margins. More details of Hyperparameters of Q-Learning used in Behaviour Explorer can be accessed here [Hyperparameter setting](https://github.com/ABCRepository/Repository/blob/main/Hyperparameters/hyperparameters.png).

## Demonstration of Running example with SysML ##
![image](https://github.com/ABCRepository/Repository/blob/main/Running%20example%20SysML%20model/running%20example.png)

