# Safety Behaviour Abstraction and Model Evolution in Autonomous Driving

This repository contains:

**1. REMEDY Tool** -  The REMEDY Tool, which includes ETM-Executor and ETM-Evolver for enabling the execution and evolution of the ETM;

**2. MEDY Profile** - The MEDY Profile define three profiles to distinguish three types of variables, two types of operations, and three behaviours;

**3. Running Example** - The Running example with a SysML model, including a block definition diagram and a state machine, to capture the behaviour of driving through a block;

**4. Data Sets** - All the raw data for two experiments.

## Contributions ##
**1.** proposed a novel approach (REMEDY) for automatically discovering previously unknown ADS behaviours and specifying them as executable state machines.

**2.** proposed a solution to evolve state machines via model execution and co-simulation constantly.

**3.** Implemented risk-based strategy RiOT using Q-learning to drive model execution, model evolution, and co-simulation.

**4.** conduct an empirical evaluation to assess the cost-effectiveness of the strategy by comparing it with three comparision baselines, i.e., random strategy
(RT ), a coverage-based greedy strategy (COT ), and a start-of-art RL-based approach DeepCollision. Results show
that RiOT significantly outperforms COT and RT in discovering risky behaviours and COT performs the best in generating diverse scenarios. Moreover, REMEDY with RiOT can generate scenarios that lead to
a collision earlier, have higher overall collision velocity, and are more diverse, as compared with DeepCollision.

**5.** implemented REMEDY with the three strategies (i.e., RiOT, COT and RT) that support model execution and evolution as a prototype.

## Overview of REMEDY ##
An overview of REMEDY is shown here. REMEDY aims to execute executable test models (in SysML state machines) deployed on SysML-based modelling and model execution environment Papyrus and
Moka, and the virtual vehicle deployed on a simulator (in our case, CARLA) to evolve test models for discovering previously-unknown behaviours. REMEDY is equiped with two components (i.e., ETM-Executor
and ETM-Evolver). ETM-Executor is used to execute ETM and ETM-Evolver is used to evolve ETM.
![image](https://github.com/ABCRepository/Repository/blob/main/Overview%20of%20REMEDY/overview%20of%20framework1.png)

## REMEDY Environment Configuration ##
**1. Construct initial ETM model**

To construct an ETM (i.e., executable test model) using a SysML model, begin by building a block definition diagram and a state machine diagram, and then apply defined MEDY profiles to the ETM based on [Papyrus](https://projects.eclipse.org/projects/modeling.mdt.papyrus)

**2. Configure CARLA setting**

Using APIs for getting corresponding attributes of the virtual vehicle and environment (e.g., speed), controlling the execution of the virtual vehicle (e.g. turnLeft), and configuring the dynamic experiment environment (e.g., adding NPC vehicles around the virtual vehicle and setting clear weather), the detailed instructions please look at [CARLA](https://carla.readthedocs.io/en/latest/python_api).

## Demonstration of Running example with SysML ##
![image](https://github.com/ABCRepository/Repository/blob/main/Running%20example%20SysML%20model/running%20example.png)

