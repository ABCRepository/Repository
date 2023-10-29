# Behaviours Abstraction and Model Evolution in Autonomous Driving via Co-simulation

This repository contains:

**1. REMEDY Tool** -  The REMEDY Tool, which includes ETM-Executor (related to SysMLEvolverExecutor.java)  and ETM-Evolver (related to DiagramUpdaterUtil.java) for enabling the execution and evolution of the ETM;

**2. MEDY Profiles** - The MEDY Profiles define three profiles to distinguish three types of variables, two types of operations, and three behaviors;

**3. Running Example** - The Running example with SysML model(i.e., a block definition diagram and a state machine) to capture behavior of driving through a block;

**4. Data Sets** - All the raw data for two experiments.

## Contributions ##
**1.** proposed a novel approach (REMEDY) for automatically discovering previously unknown ADS behaviours and specifying them as executable state machines.

**2.** proposed a solution to evolve state machines via model execution and co-simulation constantly.

**3.** Implemented risk-based strategy RiOT using Q-learning to drive model execution, model evolution, and co-simulation.

**4.** conducted an empirical evaluation to assess the cost-effectiveness of the strategy by comparing it with a random strategy (RT) and a coveragebased greedy strategy (COT) – the baselines. Results show
that RiOT outperforms RT and COT in discovering risky ADS behaviours, while COT performs the best in exploring more diverse behaviours.

**5.** implemented REMEDY with the three strategies (i.e., RiOT, COT and RT) and model execution as a prototype.

## Overview of REMEDY ##
An overview of REMEDY is shown here. REMEDY aims to execute executable test models (in SysML state machines) deployed on SysML-based modelling and model execution environment Papyrus and
Moka, and the virtual vehicle deployed on a simulator (in our case, CARLA) to evolve test models for discovering previously-unknown behaviours. REMEDY is equiped with two components (i.e., ETM-Executor
and ETM-Evolver). ETM-Executor is used to execute ETM and ETM-Evolver is used to evolve ETM.
![image](https://github.com/ABCRepository/Repository/blob/main/Overview%20of%20REMEDY/Overview%20of%20framework.png)

## REMEDY Environment Configuration ##
**1. Construct initial ETM model**

To construct an ETM (i.e., SysML model) with a block definition diagram and a state machine diagram, and then apply REDY profiles to the ETM.

**2. Configure CARLA setting**

To see detailed instructions for using APIs for getting corresponding attributes of the virtual vehicle and environment and configuring the experiment environment (e.g., add NPC vehicles within predefined regions and set clear weather), please look at [CARLA](https://carla.readthedocs.io/en/latest/python_api).

## Demonstration of Running example with SysML ##
![image](https://github.com/ABCRepository/Repository/blob/main/Running%20example%20SysML%20model/running%20example.png)

