# Model-based Test Environment Development for Autonomous Driving Systems

This repository contains:

**1. REMEDY Tool** -  The REMEDY Tool, which includes ETM-Executor and ETM-Evolver for enabling the execution and evolution of the ETM;

**2. MEDY Profiles** - The MEDY Profiles define two profiles to distinguish three types of variables and two types of operations;

**3. Running Example** - The Running example with SysML model(i.e., a blcok definition diagram and a state machine) to capture behavior of driving through a block;

**4. Data Sets** - All the raw data and plots for two experiments.

## Contributions ##
**1.** We introduce a novel approach with a tool, named REMEDY, for automatically discovering previously-unknown ADS behaviours and specifying them as state machines.

**2.** We implemented three strategies, for REMEDY, all integrated with REMEDY to drive model execution, evolution, and co-simulation.

**3.** We conducted an empirical evaluation to assess the cost-effectiveness of these three strategies. Results show that the risk-based strategy outperforms the random and
coverage-based strategies in discovering risky ADS behaviours and driving scenarios.

## Overview of REMEDY ##
An overview of REMEDY is shown here. REMEDY aims to execute executable test models (in SysML state machines) deployed on SysML-based modelling and model execution environment Papyrus and
Moka, and the virtual vehicle deployed on a simulator (in our case, CARLA) to evolve test models for discovering previously-unknown behaviours. REMEDY is equiped with two components (i.e., ETM-Executor
and ETM-Evolver). ETM-Executor is used to execute ETM and ETM-Evolver is used to evolve ETM.
![image](https://github.com/ChaoTan201/Evolver-Repository/blob/main/Overview%20of%20REMEDY/Overview%20of%20framework.png)

## REMEDY Environment Configuration ##
**1. Construct initial ETM model**

To construct a ETM (i.e., SysML model) with a block definition diagram and a state machine diagram, and then apply REDY profiles to the ETM.

**2. Configurate CARLA setting**

To see detailed instruction of using APIs for getting corresponing attributes, please look at [CARLA](https://carla.readthedocs.io/en/latest/python_api).

