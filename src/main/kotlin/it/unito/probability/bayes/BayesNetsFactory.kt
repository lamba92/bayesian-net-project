@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_VARIABLE")

package it.unito.probability.bayes

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.Node
import aima.core.probability.bayes.impl.BayesNet
import aima.core.probability.bayes.impl.DynamicBayesNet
import aima.core.probability.bayes.impl.FullCPTNode
import aima.core.probability.domain.BooleanDomain
import aima.core.probability.util.RandVar

/**
 * Example of [DynamicBayesNet]
 */
@Suppress("LocalVariableName")
object BayesNetsFactory{

    fun getComplexDynamicNetworkExample(): DynamicBayesNet {
        val r_0 = RandVar("R_0", BooleanDomain())
        val s_0 = RandVar("S_0", BooleanDomain())

        val r_1 = RandVar("R_1", BooleanDomain())
        val s_1 = RandVar("S_1", BooleanDomain())

        val e_1 = RandVar("E_1",BooleanDomain())

        val priorNetwork = BayesNet(
                FullCPTNode(r_0, doubleArrayOf(0.5, 0.5)),
                FullCPTNode(s_0, doubleArrayOf(0.5, 0.5))
        )

        val r_0_node = FullCPTNode(r_0, doubleArrayOf(0.5, 0.5))
        val s_0_node = FullCPTNode(s_0, doubleArrayOf(0.5, 0.5))

        val r_1_node = FullCPTNode(r_1, doubleArrayOf(
                    0.1, 0.9,
                    0.15, 0.85,
                    0.7, 0.3,
                    0.8, 0.2
            ), r_0_node, s_0_node)
        val s_1_node = FullCPTNode(s_1, doubleArrayOf(
                    0.23, 0.77,
                    0.4, 0.6,
                    0.57, 0.43,
                    0.29, 0.71
                ), r_0_node, s_0_node)
        val e_1_node = FullCPTNode(e_1, doubleArrayOf(
                    0.93, 0.07,
                    0.49, 0.51,
                    0.87, 0.13,
                    0.01, 0.99
        ), r_1_node, s_1_node)

        val rvMap = HashMap<RandomVariable, RandomVariable>().apply {
            this[r_0] = r_1
            this[s_0] = s_1
        }
        val evidences = HashSet<RandomVariable>().apply { add(e_1) }
        return DynamicBayesNet(priorNetwork, rvMap, evidences, r_0_node, s_0_node)
    }

    fun getDigitalCircuitNetExample(): BayesianNetwork {
        val j = RandVar("J", BooleanDomain())
        val i = RandVar("I", BooleanDomain())
        val y = RandVar("Y", BooleanDomain())
        val x = RandVar("X", BooleanDomain())
        val o = RandVar("O", BooleanDomain())

        val j_node = FullCPTNode(j, doubleArrayOf(0.5, 0.5))
        val i_node = FullCPTNode(i, doubleArrayOf(0.5, 0.5))

        val y_node = FullCPTNode(y, doubleArrayOf(
                0.01, 0.99,
                0.99, 0.01),
                j_node
        )

        val x_node = FullCPTNode(x, doubleArrayOf(
                0.95, 0.05,
                0.05, 0.95,
                0.05, 0.95,
                0.05, 0.95),
                j_node, i_node
        )

        val o_node = FullCPTNode(o, doubleArrayOf(
                0.98, 0.02,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                y_node, x_node
        )
        return BayesNet(j_node, i_node)
    }

    fun getAdderNetExample(): BayesianNetwork {
        val a = RandVar("A", BooleanDomain())
        val b = RandVar("B", BooleanDomain())
        val c_in = RandVar("C_IN", BooleanDomain())
        val xor = RandVar("XOR", BooleanDomain())
        val and1 = RandVar("AND1", BooleanDomain())
        val and2 = RandVar("AND2", BooleanDomain())
        val s = RandVar("S", BooleanDomain())
        val c_out = RandVar("C_OUT", BooleanDomain())

        val a_node = FullCPTNode(a, doubleArrayOf(0.5, 0.5))
        val b_node = FullCPTNode(b, doubleArrayOf(0.5, 0.5))
        val cIN_node = FullCPTNode(c_in, doubleArrayOf(0.5, 0.5))

        val xor_node = FullCPTNode(xor, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                a_node, b_node
        )

        val and_node1 = FullCPTNode(and1, doubleArrayOf(
                0.95, 0.05,
                0.05, 0.95,
                0.05, 0.95,
                0.05, 0.95),
                xor_node, cIN_node
        )

        val and_node2 = FullCPTNode(and2, doubleArrayOf(
                0.95, 0.05,
                0.05, 0.95,
                0.05, 0.95,
                0.05, 0.95),
                a_node, b_node
        )

        val s_node = FullCPTNode(s, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                xor_node, cIN_node
        )

        val cOut_node = FullCPTNode(c_out, doubleArrayOf(
                0.98, 0.02,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                and_node1, and_node2
        )
        return BayesNet(a_node, b_node, cIN_node)
    }

    fun getFullAdderCircuitNet(): BayesianNetwork {

        // Random Variables
        val a = RandVar("A_0", BooleanDomain())
        val b = RandVar ("B_0", BooleanDomain())
        val carryIn = RandVar("CarryIn_-1", BooleanDomain())

        // Roots
        val aRootNode = FullCPTNode(a, doubleArrayOf(0.5, 0.5))
        val bRootNode = FullCPTNode(b, doubleArrayOf(0.5, 0.5))
        val carryInRootNode = FullCPTNode(carryIn, doubleArrayOf(0.5, 0.5))
        val rootList = arrayListOf<Node>(aRootNode, bRootNode, carryInRootNode)

        //Nodes
        var aNode = aRootNode
        var bNode = bRootNode
        var carryInNode = carryInRootNode

        for (i in 1..1){ //8962
            val sumTmp = RandVar ("Sum_$i", BooleanDomain())
            val sumNode = FullCPTNode(sumTmp, doubleArrayOf(
                    0.99, 0.01,
                    0.01, 0.99,
                    0.01, 0.99,
                    0.99, 0.01,
                    0.01, 0.99,
                    0.99, 0.01,
                    0.99, 0.01,
                    0.01, 0.99),
                    aNode, bNode, carryInNode
            )

            val carryOutTmp = RandVar ("""CarryOut_${i - 1}""", BooleanDomain())
            val carryOutNode = FullCPTNode(carryOutTmp, doubleArrayOf(
                    0.99, 0.01,
                    0.99, 0.01,
                    0.99, 0.01,
                    0.01, 0.99,
                    0.99, 0.01,
                    0.01, 0.99,
                    0.01, 0.99,
                    0.01, 0.99),
                    aNode, bNode, carryInNode
            )

        val aTmp = RandVar("A_$i", BooleanDomain())
        val bTmp = RandVar ("B_$i", BooleanDomain())
        val carryInTmp = RandVar("CarryIn_$i", BooleanDomain())

        aNode = FullCPTNode(aTmp, doubleArrayOf(0.5, 0.5))
        bNode = FullCPTNode(bTmp, doubleArrayOf(0.5, 0.5))
        carryInNode = FullCPTNode(carryInTmp, doubleArrayOf(0.99, 0.01, 0.01, 0.99), carryOutNode)
        rootList.apply { this.add(aNode); this.add(bNode);}
        }
        return BayesNet(*rootList.toTypedArray())
    }

    fun getChildNet(): BayesianNetwork {

        /**
         * [RandomVariable]s declaration
         */
        val birthAsphyxia = RandVar("BIRTH_ASPHYXIA", BooleanDomain())
        val hypDistrib = RandVar("HYP_DISTRIB", BooleanDomain())
        val hypoxiaInO2 = RandVar("HYPOXIA_IN_O2", BooleanDomain())
        val co2 = RandVar("CO2", BooleanDomain())
        val chestXray = RandVar("CHEST_XRAY", BooleanDomain())
        val grunting = RandVar("GRUNTING", BooleanDomain())
        val lVHreport = RandVar("LVH_REPORT", BooleanDomain())
        val lowerBodyO2 = RandVar("LOWER_BODY_O2", BooleanDomain())
        val rUQO2 = RandVar("RUQ_O2", BooleanDomain())
        val cO2Report = RandVar("CO2_REPORT", BooleanDomain())
        val xrayReport = RandVar("XRAY_REPORT", BooleanDomain())
        val disease = RandVar("DISEASE", BooleanDomain())
        val gruntingReport = RandVar("GRUNTING_REPORT", BooleanDomain())
        val age = RandVar("AGE", BooleanDomain())
        val lVH = RandVar("LVH", BooleanDomain())
        val ductFlow = RandVar("DUCT_FLOW", BooleanDomain())
        val cardiacMixing = RandVar("CARDIAC_MIXING", BooleanDomain())
        val lungParench = RandVar("LUNG_PARENCH", BooleanDomain())
        val lungFlow = RandVar("LUNG_FLOW", BooleanDomain())
        val sick = RandVar("SICK", BooleanDomain())

        /**
         * [FullCPTNode]s declaration
         */
        val birthAsphyxia_node = FullCPTNode(birthAsphyxia, doubleArrayOf(0.1, 0.9))

        val disease_node = FullCPTNode(disease, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                birthAsphyxia_node
        )

        val lVH_node = FullCPTNode(lVH, doubleArrayOf(
                0.9, 0.1,
                0.05, 0.95),
                disease_node
        )

        val ductFlow_node = FullCPTNode(ductFlow, doubleArrayOf(
                0.2, 0.8,
                0.66, 0.34),
                disease_node
        )

        val cardiacMixing_node = FullCPTNode(cardiacMixing, doubleArrayOf(
                0.83, 0.17,
                0.04, 0.96),
                disease_node
        )

        val lungParench_node = FullCPTNode(lungParench, doubleArrayOf(
                0.6, 0.4,
                0.1, 0.9),
                disease_node
        )

        val lungFlow_node = FullCPTNode(lungFlow, doubleArrayOf(
                0.15, 0.85,
                0.3, 0.7),
                disease_node
        )

        val sick_node = FullCPTNode(sick, doubleArrayOf(
                0.2, 0.8,
                0.7, 0.3),
                disease_node
        )

        val age_node = FullCPTNode(age, doubleArrayOf(
                0.95, 0.05,
                0.25, 0.75,
                0.63, 0.37,
                0.55, 0.45),
                disease_node, sick_node
        )

        val grunting_node = FullCPTNode(grunting, doubleArrayOf(
                0.2, 0.8,
                0.4, 0.6,
                0.8, 0.2,
                0.2, 0.8),
                lungParench_node, sick_node
        )

        val co2_node = FullCPTNode(co2, doubleArrayOf(
                0.8, 0.2,
                0.5, 0.5),
                lungParench_node
        )

        val chestXray_node = FullCPTNode(chestXray, doubleArrayOf(
                0.93, 0.07,
                0.94, 0.06,
                0.17, 0.83,
                0.24, 0.76),
                lungParench_node, lungFlow_node
        )

        val hypDistrib_node = FullCPTNode(hypDistrib, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.95, 0.05,
                0.5, 0.5),
                ductFlow_node, cardiacMixing_node
        )


        val hypoxiaInO2_node = FullCPTNode(hypoxiaInO2, doubleArrayOf(
                0.93, 0.07,
                0.9, 0.1,
                0.70, 0.3,
                0.35, 0.65),
                cardiacMixing_node, lungParench_node
        )

        val lVHreport_node = FullCPTNode(lVHreport, doubleArrayOf(
                0.2, 0.8,
                0.05, 0.95),
                lVH_node
        )

        val lowerBodyO2_node = FullCPTNode(lowerBodyO2, doubleArrayOf(
                0.4, 0.6,
                0.5, 0.5,
                0.1, 0.9,
                0.6, 0.4),
                hypDistrib_node, hypoxiaInO2_node
        )

        val rUQO2_node = FullCPTNode(rUQO2, doubleArrayOf(
                0.1, 0.9,
                0.3, 0.7),
                hypoxiaInO2_node
        )

        val cO2Report_node = FullCPTNode(cO2Report, doubleArrayOf(
                0.9, 0.1,
                0.1, 0.9),
                co2_node
        )

        val xrayReport_node = FullCPTNode(xrayReport, doubleArrayOf(
                0.7, 0.3,
                0.1, 0.9),
                chestXray_node
        )

        val gruntingReport_node = FullCPTNode(gruntingReport, doubleArrayOf(
                0.8, 0.2,
                0.1, 0.9),
                grunting_node
        )

        return BayesNet(birthAsphyxia_node)
    }


    fun getAlarmNet(): BayesianNetwork{

        /**
         * [RandomVariable]s declaration
         */
        val history = RandVar("HISTORY", BooleanDomain())
        val cvp = RandVar("CVP", BooleanDomain())
        val pcwp = RandVar("PCWP", BooleanDomain())
        val hypovolemia = RandVar("HYPOVOLEMIA", BooleanDomain())
        val lvedvolume = RandVar("LVEDVOLUME", BooleanDomain())
        val lvfailure = RandVar("LVFAILURE", BooleanDomain())
        val strokevolume = RandVar("STROKEVOLUME", BooleanDomain())
        val errlowoutput = RandVar("ERRLOWOUTPUT", BooleanDomain())

        val hrbp = RandVar("HRBP", BooleanDomain())
        val hrekg = RandVar("HREKG", BooleanDomain())
        val errcauter = RandVar("ERRCAUTER", BooleanDomain())
        val hrsat = RandVar("HRSAT", BooleanDomain())
        val insuffanesth = RandVar("INSUFFANESTH", BooleanDomain())
        val anaphylaxis = RandVar("ANAPHYLAXIS", BooleanDomain())
        val tpr = RandVar("TPR", BooleanDomain())
        val expco2 = RandVar("EXPCO2", BooleanDomain())

        val kinkedtube = RandVar("KINKEDTUBE", BooleanDomain())
        val minvol = RandVar("MINVOL", BooleanDomain())
        val fio2 = RandVar("FIO2", BooleanDomain())
        val pvsat = RandVar("PVSAT", BooleanDomain())
        val sao2 = RandVar("SAO2", BooleanDomain())
        val pap = RandVar("PAP", BooleanDomain())
        val pulmembolus = RandVar("PULMEMBOLUS", BooleanDomain())
        val shunt = RandVar("SHUNT", BooleanDomain())

        val intubation = RandVar("INTUBATION", BooleanDomain())
        val press = RandVar("PRESS", BooleanDomain())
        val disconnect = RandVar("DISCONNECT", BooleanDomain())
        val minvolset = RandVar("MINVOLSET", BooleanDomain())
        val ventmach = RandVar("VENTMACH", BooleanDomain())
        val venttube = RandVar("VENTTUBE", BooleanDomain())
        val ventlung = RandVar("VENTLUNG", BooleanDomain())
        val ventalv = RandVar("VENTALV", BooleanDomain())

        val artco2 = RandVar("ARTCO2", BooleanDomain())
        val catechol = RandVar("CATECHOL", BooleanDomain())
        val hr = RandVar("HR", BooleanDomain())
        val co = RandVar("CO", BooleanDomain())
        val bp = RandVar("BP", BooleanDomain())

        /**
         * [FullCPTNode]s declaration
         */
        val minvolset_node = FullCPTNode(minvolset, doubleArrayOf(0.5, 0.5))
        val hypovolemia_node = FullCPTNode(hypovolemia, doubleArrayOf(0.2, 0.8))
        val lvfailure_node = FullCPTNode(lvfailure, doubleArrayOf(0.05, 0.95))
        val errlowoutput_node = FullCPTNode(errlowoutput, doubleArrayOf(0.05, 0.95))
        val errcauter_node = FullCPTNode(errcauter, doubleArrayOf(0.1, 0.9))
        val insuffanesth_node = FullCPTNode(insuffanesth, doubleArrayOf(0.1, 0.9))
        val anaphylaxis_node = FullCPTNode(anaphylaxis, doubleArrayOf(0.01, 0.99))
        val kinkedtube_node = FullCPTNode(kinkedtube, doubleArrayOf(0.04, 0.96))
        val fio2_node = FullCPTNode(fio2, doubleArrayOf(0.05, 0.95))
        val pulmembolus_node = FullCPTNode(pulmembolus, doubleArrayOf(0.01, 0.99))
        val intubation_node = FullCPTNode(intubation, doubleArrayOf(0.92, 0.08))
        val disconnect_node = FullCPTNode(disconnect, doubleArrayOf(0.1, 0.9))

        var catechol_node = FullCPTNode(catechol, doubleArrayOf(0.5, 0.5))
        var artco2_node = FullCPTNode(artco2, doubleArrayOf(0.5, 0.5))
        var ventlung_node = FullCPTNode(ventlung, doubleArrayOf(0.5, 0.5))


        val history_node = FullCPTNode(history, doubleArrayOf(
                0.9, 0.1,
                0.01, 0.99
        ), lvfailure_node)

        val lvedvolume_node = FullCPTNode(lvedvolume, doubleArrayOf(
                0.95, 0.05,
                0.98, 0.02,
                0.01, 0.99,
                0.05, 0.95
        ), hypovolemia_node, lvfailure_node)

        val cvp_node =  FullCPTNode(cvp, doubleArrayOf(
                0.85, 0.15,
                0.29, 0.71
        ), lvedvolume_node)

        val pcwp_node =  FullCPTNode(pcwp, doubleArrayOf(
                0.85, 0.15,
                0.05, 0.95
        ), lvedvolume_node)

        val strokevolume_node = FullCPTNode(strokevolume, doubleArrayOf(
                0.98, 0.02,
                0.95, 0.05,
                0.45, 0.55,
                0.05, 0.95
        ), hypovolemia_node, lvfailure_node)

        val hr_node = FullCPTNode(hr, doubleArrayOf(
                0.85, 0.15,
                0.05, 0.95
        ), history_node)

        val hrbp_node = FullCPTNode(hrbp, doubleArrayOf(
                0.98, 0.02,
                0.40, 0.60,
                0.30, 0.70,
                0.02, 0.98
        ), errlowoutput_node, hr_node)

        val hrekg_node = FullCPTNode(hrekg, doubleArrayOf(
                0.34, 0.66,
                0.34, 0.66,
                0.98, 0.02,
                0.01, 0.99
        ), errcauter_node, hr_node)

        val hrsat_node = FullCPTNode(hrsat, doubleArrayOf(
                0.34, 0.66,
                0.34, 0.66,
                0.98, 0.02,
                0.01, 0.99
        ), errcauter_node, hr_node)

        val tpr_node = FullCPTNode(tpr, doubleArrayOf(
                0.98, 0.02,
                0.3, 0.7
        ), anaphylaxis_node)

        val expco2_node = FullCPTNode(expco2, doubleArrayOf(
                0.97, 0.03,
                0.98, 0.02,
                0.02, 0.98,
                0.01, 0.99
        ), artco2_node, ventlung_node)

        val ventmach_node = FullCPTNode(ventmach, doubleArrayOf(
                0.98, 0.02,
                0.06, 0.94
        ), minvolset_node)

        val venttube_node = FullCPTNode(venttube, doubleArrayOf(
                0.98, 0.02,
                0.95, 0.05,
                0.90, 0.1,
                0.69, 0.31
        ), disconnect_node, ventmach_node)

        ventlung_node = FullCPTNode(ventlung, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.02, 0.98,
                0.98, 0.02,
                0.3, 0.7,
                0.25, 0.75,
                0.98, 0.02,
                0.39, 0.61
        ), intubation_node, kinkedtube_node, venttube_node)

        val minvol_node = FullCPTNode(minvol, doubleArrayOf(
                0.97, 0.03,
                0.98, 0.02,
                0.02, 0.98,
                0.01, 0.99
        ), intubation_node, ventlung_node)

        val ventalv_node = FullCPTNode(ventalv, doubleArrayOf(
                0.97, 0.03,
                0.98, 0.02,
                0.02, 0.98,
                0.12, 0.88
        ), intubation_node, ventlung_node)

        val pvsat_node = FullCPTNode(pvsat, doubleArrayOf(
                1.00, 0.00,
                0.99, 0.01,
                1.00, 0.00,
                0.02, 0.98
        ), fio2_node, ventalv_node)

        val shunt_node = FullCPTNode(shunt, doubleArrayOf(
                0.1, 0.9,
                0.1, 0.9,
                0.95, 0.05,
                0.05, 0.95
        ), intubation_node, pulmembolus_node)

        val sao2_node = FullCPTNode(sao2, doubleArrayOf(
                0.98, 0.02,
                0.95, 0.05,
                0.90, 0.1,
                0.69, 0.31
        ), pvsat_node, shunt_node)

        val pap_node = FullCPTNode(pap, doubleArrayOf(
                0.2, 0.8,
                0.05, 0.95
        ), pulmembolus_node)

        val press_node = FullCPTNode(press, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.02, 0.98,
                0.98, 0.02,
                0.3, 0.7,
                0.25, 0.75,
                0.98, 0.02,
                0.39, 0.61
        ), intubation_node, kinkedtube_node, venttube_node)

        artco2_node = FullCPTNode(artco2, doubleArrayOf(
                0.02, 0.98,
                0.90, 0.10
        ), ventalv_node)

        catechol_node = FullCPTNode(catechol, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.02, 0.98,
                0.98, 0.02,
                0.3, 0.7,
                0.25, 0.75,
                0.98, 0.02,
                0.39, 0.61,
                0.55, 0.45,
                0.17, 0.83,
                0.02, 0.98,
                0.98, 0.02,
                0.44, 0.56,
                0.25, 0.75,
                0.98, 0.02,
                0.19, 0.81
        ), artco2_node, insuffanesth_node, sao2_node, tpr_node)

        val co_node = FullCPTNode(co, doubleArrayOf(
                0.98, 0.02,
                0.95, 0.05,
                0.31, 0.69,
                0.02, 0.98
        ), hr_node, strokevolume_node)

        val bp_node = FullCPTNode(bp, doubleArrayOf(
                0.98, 0.02,
                0.98, 0.02,
                0.15, 0.85,
                0.45, 0.55
        ), co_node, tpr_node)

        return BayesNet(hypovolemia_node, lvfailure_node, errlowoutput_node, errcauter_node, insuffanesth_node,
                        anaphylaxis_node, kinkedtube_node, fio2_node, pulmembolus_node, intubation_node,
                        disconnect_node, minvolset_node)
    }

    fun getAndesNet(): BayesianNetwork{return BayesNet()}

    fun getHailfinderNet(): BayesianNetwork{return BayesNet()}

    fun getSachsNet(): BayesianNetwork{return BayesNet()}

}