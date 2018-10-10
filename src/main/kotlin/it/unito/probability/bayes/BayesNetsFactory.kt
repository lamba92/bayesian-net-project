@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_VARIABLE")

package it.unito.probability.bayes

import aima.core.probability.RandomVariable
import aima.core.probability.bayes.BayesianNetwork
import aima.core.probability.bayes.Node
import aima.core.probability.bayes.impl.BayesNet
import aima.core.probability.bayes.impl.DynamicBayesNet
import aima.core.probability.bayes.impl.FullCPTNode
import aima.core.probability.domain.BooleanDomain
import aima.core.probability.domain.DiscreteDomain
import aima.core.probability.domain.FiniteDomain
import aima.core.probability.domain.FiniteIntegerDomain
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
        /**
         * Number of nodes: 5
         * Number of arcs: 5
         */

        /**
         * [RandomVariable]s declaration
         */
        val j = RandVar("J", BooleanDomain())
        val i = RandVar("I", BooleanDomain())
        val y = RandVar("Y", BooleanDomain())
        val x = RandVar("X", BooleanDomain())
        val o = RandVar("O", BooleanDomain())

        /**
         * [FullCPTNode]s declaration
         */
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
        /**
         * Number of nodes: 8
         * Number of arcs: 10
         */

        /**
         * [RandomVariable]s declaration
         */
        val a = RandVar("A", BooleanDomain())
        val b = RandVar("B", BooleanDomain())
        val c_in = RandVar("C_IN", BooleanDomain())
        val xor = RandVar("XOR", BooleanDomain())
        val and1 = RandVar("AND1", BooleanDomain())
        val and2 = RandVar("AND2", BooleanDomain())
        val s = RandVar("S", BooleanDomain())
        val c_out = RandVar("C_OUT", BooleanDomain())

        /**
         * [FullCPTNode]s declaration
         */
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

   /* fun getFullAdderCircuitNet(): BayesianNetwork {

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
    }*/

    fun getSachsNet(): BayesianNetwork{
        /**
         * Number of nodes: 11
         * Number of arcs: 17
         */

        /**
         * [RandomVariable]s declaration
         */
        val akt = RandVar("AKT", BooleanDomain())
        val erk = RandVar("ERK", BooleanDomain())
        val jnk = RandVar("JNK", BooleanDomain())
        val mek = RandVar("MEK", BooleanDomain())
        val p38 = RandVar("P38", BooleanDomain())
        val pIP2 = RandVar("PIP2", BooleanDomain())
        val pIP3 = RandVar("PIP3", BooleanDomain())
        val pKA = RandVar("PKA", BooleanDomain())
        val pKC = RandVar("PKC", BooleanDomain())
        val plcg = RandVar("PLCG", BooleanDomain())
        val raf = RandVar("RAF", BooleanDomain())

        /**
         * [FullCPTNode]s declaration
         */
        val pKC_node = FullCPTNode(pKC, doubleArrayOf(0.5, 0.5))
        val plcg_node = FullCPTNode(plcg, doubleArrayOf(0.5, 0.5))

        val pKA_node = FullCPTNode(pKA, doubleArrayOf(
                0.76, 0.24,
                0.015, 0.985),
                pKC_node
        )

        val raf_node = FullCPTNode(raf, doubleArrayOf(
                0.19, 0.81,
                0.44, 0.56,
                0.75, 0.25,
                0.86, 0.14),
                pKA_node, pKC_node
        )

        val mek_node =FullCPTNode(mek, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.02, 0.98,
                0.98, 0.02,
                0.3, 0.7,
                0.25, 0.75,
                0.98, 0.02,
                0.39, 0.61
        ), pKA_node, pKC_node, raf_node)

        val erk_node = FullCPTNode(erk, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                mek_node, pKA_node
        )

        val akt_node = FullCPTNode(akt, doubleArrayOf(
                0.85, 0.15,
                0.39, 0.61,
                0.05, 0.95,
                0.0035, 0.9965),
                erk_node, pKA_node
        )

        val jnk_node = FullCPTNode(jnk, doubleArrayOf(
                0.29, 0.71,
                0.58, 0.42,
                0.86, 0.14,
                0.044, 0.956),
                pKA_node, pKC_node
        )

        val p38_node = FullCPTNode(p38, doubleArrayOf(
                0.3, 0.7,
                0.91, 0.09,
                0.38, 0.62,
                0.8, 0.2),
                pKA_node, pKC_node
        )

        val pIP3_node = FullCPTNode(pIP3, doubleArrayOf(
                0.21, 0.79,
                0.42, 0.58),
                plcg_node
        )

        val pIP2_node = FullCPTNode(pIP2, doubleArrayOf(
                0.87, 0.13,
                0.99, 0.01,
                0.52, 0.48,
                0.02, 0.98),
                pIP3_node, plcg_node
        )

        return BayesNet(pKC_node, plcg_node)}

    fun getChildNet(): BayesianNetwork {
        /**
         * Number of nodes: 20
         * Number of arcs: 25
         */

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
         * Number of nodes: 37
         * Number of arcs: 46
         */

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

    fun getHailfinderNet(): BayesianNetwork{
        /**
         * Number of nodes: 56
         * Number of arcs: 66
         */

        /**
         * [RandomVariable]s declaration
         */
        val n0_7muVerMo = RandVar("N0_7MU_VERMO", BooleanDomain())
        val subjVertMo = RandVar("SUBJ:VERTMO", BooleanDomain())
        val qGVertMotion = RandVar("QG_VERTMOTION", BooleanDomain())
        val combVerMo = RandVar("COMB_VERMO", BooleanDomain())
        val areaMeso_ALS = RandVar("AREAMESO_ALS", BooleanDomain())
        val satContMoist = RandVar("SATCONTMOIST", BooleanDomain())
        val raoContMoist = RandVar("RAOCONTMOIST", BooleanDomain())
        val combMoisture = RandVar("COMB_MOISTURE", BooleanDomain())

        val areaMoDryAir = RandVar("AREAMO_DRYAIR", BooleanDomain())
        val vISCloudCov = RandVar("VIS_CLOUDCOV", BooleanDomain())
        val iRCloudCover = RandVar("IR_CLOUDCOV", BooleanDomain())
        val combClouds = RandVar("COMB_CLOUDS", BooleanDomain())
        val cldShadeOth = RandVar("CLD_SHADE_OTH", BooleanDomain())
        val aMInstabMt = RandVar("AN_INSTAB_MT", BooleanDomain())
        val insInMt = RandVar("INS_IN_MT", BooleanDomain())
        val wndHodograph = RandVar("WND_HODOGRAPH", BooleanDomain())

        val outflowFrMt = RandVar("OUTFLOW_FRMT", BooleanDomain())
        val morningBound = RandVar("MORNING_BOUND", BooleanDomain())
        val boundaries = RandVar("BOUNDARIES", BooleanDomain())
        val cldShadeConv = RandVar("CLD_SHADE_CONV", BooleanDomain())
        val compPlFcst = RandVar("COMPL_FCST", BooleanDomain())
        val capChange = RandVar("CAP_CHANGE", BooleanDomain())
        val loLevMoistAd = RandVar("LOLEV_MOIST_AD", BooleanDomain())
        val insChange = RandVar("INS_CHANGE", BooleanDomain())

        val mountainFcst = RandVar("MOUNTAIN_FCST", BooleanDomain())
        val date = RandVar("DATE", BooleanDomain())
        val scenario = RandVar("SCENARIO", BooleanDomain())
        val scenRelAMCIN = RandVar("SCEN_REL_AMCIN", BooleanDomain())
        val morningCIN = RandVar("MORNING_CIN", BooleanDomain())
        val aMCINInScen = RandVar("AMCIN_IN_SCEN", BooleanDomain())
        val capInScen = RandVar("CAP_IN_SCEN", BooleanDomain())
        val scenRelAMIns = RandVar("SCEN_REL_AMINS", BooleanDomain())

        val lIfr12ZDENSd = RandVar("LIFR23ZDENSD", BooleanDomain())
        val aMDewptCalPl = RandVar("AMDEWPTCALPL", BooleanDomain())
        val aMInsWliScen = RandVar("AM_INS_WLISCEN", BooleanDomain())
        val insSclInScen = RandVar("INS_SCL_INSCEN", BooleanDomain())
        val scenRel3_4 = RandVar("SCEN_REL3_4", BooleanDomain())
        val latestCIN = RandVar("LATEST_CIN", BooleanDomain())
        val lLIW = RandVar("LLIW", BooleanDomain())
        val curPropConv = RandVar("CUR_PROP_CONV", BooleanDomain())

        val scnRelPlFcst = RandVar("SCN_REL_PLFCST", BooleanDomain())
        val plainsFcst = RandVar("PLAINS_FCST", BooleanDomain())
        val n34StarFcst = RandVar("N34_STAR_FCST", BooleanDomain())
        val r5Fcst = RandVar("R5_FCST", BooleanDomain())
        val dewpoints = RandVar("DEW_POINTS", BooleanDomain())
        val lowLLapse = RandVar("LOWL_LAPSE", BooleanDomain())
        val meanRH = RandVar("MEAN_RH", BooleanDomain())
        val midLLapse = RandVar("MID_LAPSE", BooleanDomain())

        val mvmtFeatures = RandVar("MVMT_FEATURES", BooleanDomain())
        val rHRatio = RandVar("RH_RATIO", BooleanDomain())
        val sfcWndShfDis = RandVar("SFC_WND_SHF_DIS", BooleanDomain())
        val synForcng = RandVar("SYN_FORCNG", BooleanDomain())
        val tempDis = RandVar("TEMP_DIS", BooleanDomain())
        val windAloft = RandVar("WIND_A_LOFT", BooleanDomain())
        val windFieldMt = RandVar("WIND_FIELD_MT", BooleanDomain())
        val windFieldPln = RandVar("WIND_FIELD_PIN", BooleanDomain())

        /**
         * [FullCPTNode]s declaration
         */
        val n0_7muVerMo_node = FullCPTNode(n0_7muVerMo, doubleArrayOf(0.5, 0.5))
        val subjVertMo_node = FullCPTNode(subjVertMo, doubleArrayOf(0.3, 0.7))
        val qGVertMotion_node = FullCPTNode(qGVertMotion, doubleArrayOf(0.3, 0.7))

        val satContMoist_node = FullCPTNode(satContMoist, doubleArrayOf(0.35, 0.65))
        val raoContMoist_node = FullCPTNode(raoContMoist, doubleArrayOf(0.35, 0.65))
        val vISCloudCov_node = FullCPTNode(vISCloudCov, doubleArrayOf(0.6, 0.4))

        val iRCloudCover_node = FullCPTNode(iRCloudCover, doubleArrayOf(0.15, 0.85))

        val aMInstabMt_node = FullCPTNode(aMInstabMt, doubleArrayOf(0.33, 0.67))
        val wndHodograph_node = FullCPTNode(wndHodograph, doubleArrayOf(0.5, 0.5))
        val morningBound_node = FullCPTNode(morningBound, doubleArrayOf(0.5, 0.5))

        val loLevMoistAd_node = FullCPTNode(loLevMoistAd, doubleArrayOf(0.4, 0.6))

        val date_node = FullCPTNode(date, doubleArrayOf(0.5, 0.5))
        val morningCIN_node = FullCPTNode(morningCIN, doubleArrayOf(0.72, 0.28))
        val lIfr12ZDENSd_node = FullCPTNode(lIfr12ZDENSd, doubleArrayOf(0.62, 0.38))

        val aMDewptCalPl_node = FullCPTNode(aMDewptCalPl, doubleArrayOf(0.55, 0.45))
        val latestCIN_node = FullCPTNode(latestCIN, doubleArrayOf(0.8, 0.2))
        val lLIW_node = FullCPTNode(lLIW, doubleArrayOf(0.44, 0.56))

        val combVerMo_node = FullCPTNode(combVerMo, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.02, 0.98,
                0.98, 0.02,
                0.3, 0.7,
                0.55, 0.45,
                0.98, 0.02,
                0.39, 0.61),
                n0_7muVerMo_node, subjVertMo_node, qGVertMotion_node
        )

        val areaMeso_ALS_node = FullCPTNode(areaMeso_ALS, doubleArrayOf(
                0.95, 0.05,
                0.05, 0.95),
                combVerMo_node
        )

        val combMoisture_node = FullCPTNode(combMoisture, doubleArrayOf(
                0.9, 0.1,
                0.55, 0.45,
                0.45, 0.55,
                0.25, 0.75),
                satContMoist_node, raoContMoist_node
        )

        val areaMoDryAir_node = FullCPTNode(areaMoDryAir, doubleArrayOf(
                0.99, 0.01,
                0.7, 0.3,
                0.4, 0.6,
                0.02, 0.98),
                areaMeso_ALS_node, combMoisture_node
        )

        val combClouds_node = FullCPTNode(combClouds, doubleArrayOf(
                0.95, 0.05,
                0.58, 0.42,
                0.3, 0.7,
                0.05, 0.95),
                vISCloudCov_node, iRCloudCover_node
        )

        val cldShadeOth_node = FullCPTNode(cldShadeOth, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.02, 0.98,
                0.98, 0.02,
                0.4, 0.6,
                0.25, 0.75,
                0.98, 0.02,
                0.39, 0.61),
                areaMoDryAir_node, areaMeso_ALS_node, combClouds_node
        )


        val insInMt_node = FullCPTNode(insInMt, doubleArrayOf(
                0.99, 0.01,
                0.7, 0.3,
                0.4, 0.6,
                0.02, 0.98),
                cldShadeOth_node, aMInstabMt_node
        )

        val outflowFrMt_node = FullCPTNode(outflowFrMt, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5,
                0.35, 0.65,
                0.08, 0.92),
                insInMt_node, wndHodograph_node
        )

        val boundaries_node = FullCPTNode(boundaries, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.02, 0.98,
                0.98, 0.02,
                0.3, 0.7,
                0.31, 0.69,
                0.98, 0.02,
                0.39, 0.61),
                outflowFrMt_node, wndHodograph_node, morningBound_node
        )

        val cldShadeConv_node = FullCPTNode(cldShadeConv, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5,
                0.35, 0.65,
                0.08, 0.92),
                insInMt_node, wndHodograph_node
        )

        val compPlFcst_node =  FullCPTNode(compPlFcst, doubleArrayOf(
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
        ), boundaries_node, cldShadeConv_node, areaMeso_ALS_node, cldShadeOth_node)

        val capChange_node = FullCPTNode(capChange, doubleArrayOf(
                0.9, 0.1,
                0.19, 0.81),
                compPlFcst_node
        )

        val insChange_node = FullCPTNode(insChange, doubleArrayOf(
                0.9, 0.1,
                0.44, 0.56,
                0.35, 0.65,
                0.08, 0.92),
                loLevMoistAd_node, compPlFcst_node
        )

        val mountainFcst_node = FullCPTNode(mountainFcst, doubleArrayOf(
                0.9, 0.1,
                0.18, 0.82),
                insInMt_node
        )

        val scenario_node = FullCPTNode(scenario, doubleArrayOf(
                0.9, 0.1,
                0.35, 0.65),
                date_node
        )

        val scenRelAMCIN_node = FullCPTNode(scenRelAMCIN, doubleArrayOf(
                0.9, 0.1,
                0.17, 0.83),
                scenario_node
        )

        val aMCINInScen_node = FullCPTNode(aMCINInScen, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5,
                0.35, 0.65,
                0.08, 0.92),
                scenRelAMCIN_node, morningCIN_node
        )

        val capInScen_node = FullCPTNode(insChange, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5,
                0.35, 0.65,
                0.08, 0.92),
                aMCINInScen_node, capChange_node
        )

        val scenRelAMIns_node = FullCPTNode(scenRelAMIns, doubleArrayOf(
                0.9, 0.1,
                0.39, 0.61),
                scenario_node
        )

        val aMInsWliScen_node = FullCPTNode(aMInsWliScen, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.02, 0.98,
                0.98, 0.02,
                0.3, 0.7,
                0.25, 0.75,
                0.98, 0.02,
                0.39, 0.61),
                scenRelAMIns_node, lIfr12ZDENSd_node, aMDewptCalPl_node
        )

        val insSclInScen_node = FullCPTNode(insSclInScen, doubleArrayOf(
                0.9, 0.1,
                0.08, 0.92),
                aMInsWliScen_node
        )

        val scenRel3_4_node = FullCPTNode(scenRel3_4, doubleArrayOf(
                0.9, 0.1,
                0.08, 0.92),
                scenario_node
        )

        val curPropConv_node = FullCPTNode(curPropConv, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5,
                0.35, 0.65,
                0.08, 0.92),
                latestCIN_node, lLIW_node
        )

        val scnRelPlFcst_node = FullCPTNode(scnRelPlFcst, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val plainsFcst_node =  FullCPTNode(plainsFcst, doubleArrayOf(
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
        ), curPropConv_node, insSclInScen_node, capInScen_node, scnRelPlFcst_node)

        val n34StarFcst_node = FullCPTNode(n34StarFcst, doubleArrayOf(
                0.9, 0.1,
                0.39, 0.61,
                0.35, 0.65,
                0.08, 0.92),
                scenRel3_4_node, plainsFcst_node
        )

        val r5Fcst_node = FullCPTNode(r5Fcst, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5,
                0.35, 0.65,
                0.08, 0.92),
                mountainFcst_node, n34StarFcst_node
        )

        val dewpoints_node = FullCPTNode(dewpoints, doubleArrayOf(
                0.9, 0.1,
                0.25, 0.75),
                scenario_node
        )

        val lowLLapse_node = FullCPTNode(lowLLapse, doubleArrayOf(
                0.9, 0.1,
                0.17, 0.83),
                scenario_node
        )

        val meanRH_node = FullCPTNode(meanRH, doubleArrayOf(
                0.9, 0.1,
                0.35, 0.65),
                scenario_node
        )

        val midLLapse_node = FullCPTNode(midLLapse, doubleArrayOf(
                0.9, 0.1,
                0.25, 0.75),
                scenario_node
        )

        val mvmtFeatures_node = FullCPTNode(mvmtFeatures, doubleArrayOf(
                0.9, 0.1,
                0.15, 0.85),
                scenario_node
        )

        val rHRatio_node = FullCPTNode(rHRatio, doubleArrayOf(
                0.9, 0.1,
                0.35, 0.65),
                scenario_node
        )

        val sfcWndShfDis_node = FullCPTNode(sfcWndShfDis, doubleArrayOf(
                0.9, 0.1,
                0.55, 0.45),
                scenario_node
        )

        val synForcng_node = FullCPTNode(synForcng, doubleArrayOf(
                0.9, 0.1,
                0.65, 0.35),
                scenario_node
        )

        val tempDis_node = FullCPTNode(tempDis, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val windAloft_node = FullCPTNode(windAloft, doubleArrayOf(
                0.9, 0.1,
                0.15, 0.85),
                scenario_node
        )

        val windFieldMt_node = FullCPTNode(windFieldMt, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val windFieldPln_node = FullCPTNode(windFieldPln, doubleArrayOf(
                0.9, 0.1,
                0.15, 0.85),
                scenario_node
        )

        return BayesNet(n0_7muVerMo_node, subjVertMo_node, qGVertMotion_node,
                satContMoist_node, raoContMoist_node, vISCloudCov_node,
                iRCloudCover_node, aMInstabMt_node, wndHodograph_node,
                morningBound_node, loLevMoistAd_node, date_node, morningCIN_node,
                lIfr12ZDENSd_node, aMDewptCalPl_node, latestCIN_node, lLIW_node)
    }}

    fun getPathfinderNet(): BayesianNetwork{
        /**
         * Number of nodes: 135
         * Number of arcs: 200
         */

        /**
         * [RandomVariable]s declaration
         */
        val fault = RandVar("FAULT", BooleanDomain())
        val f1 = RandVar("F1", BooleanDomain())
        val f97 = RandVar("F97", BooleanDomain())
        val f2 = RandVar("F2", BooleanDomain())
        val f78 = RandVar("F78", BooleanDomain())
        val f3 = RandVar("F3", BooleanDomain())
        val f4 = RandVar("F4", BooleanDomain())
        val f5 = RandVar("F5", BooleanDomain())
        val f53 = RandVar("F53", BooleanDomain())
        val f6 = RandVar("F6", BooleanDomain())
        val f7 = RandVar("F7", BooleanDomain())
        val f56 = RandVar("F56", BooleanDomain())
        val f8 = RandVar("F8", BooleanDomain())
        val f9 = RandVar("F9", BooleanDomain())
        val f10 = RandVar("F10", BooleanDomain())
        val f55 = RandVar("F55", BooleanDomain())
        val f52 = RandVar("F52", BooleanDomain())
        val f11 = RandVar("F11", BooleanDomain())
        val f12 = RandVar("F12", BooleanDomain())
        val f13 = RandVar("F13", BooleanDomain())
        val f14 = RandVar("F14", BooleanDomain())
        val f15 = RandVar("F15", BooleanDomain())
        val f16 = RandVar("F16", BooleanDomain())
        val f17 = RandVar("F17", BooleanDomain())
        val f18 = RandVar("F18", BooleanDomain())
        val f19 = RandVar("F19", BooleanDomain())
        val f41 = RandVar("F41", BooleanDomain())
        val f44 = RandVar("F44", BooleanDomain())
        val f20 = RandVar("F20", BooleanDomain())
        val f90 = RandVar("F90", BooleanDomain())
        val f21 = RandVar("F21", BooleanDomain())
        val f22 = RandVar("F22", BooleanDomain())
        val f23 = RandVar("F23", BooleanDomain())
        val f24 = RandVar("F24", BooleanDomain())
        val f25 = RandVar("F25", BooleanDomain())
        val f26 = RandVar("F26", BooleanDomain())
        val f27 = RandVar("F27", BooleanDomain())
        val f28 = RandVar("F28", BooleanDomain())
        val f92 = RandVar("F92", BooleanDomain())
        val f29 = RandVar("F29", BooleanDomain())
        val f98 = RandVar("F98", BooleanDomain())
        val f30 = RandVar("F30", BooleanDomain())
        val f31 = RandVar("F31", BooleanDomain())
        val f32 = RandVar("F32", BooleanDomain())
        val f33 = RandVar("F33", BooleanDomain())
        val f34 = RandVar("F34", BooleanDomain())
        val f35 = RandVar("F35", BooleanDomain())
        val f36 = RandVar("F36", BooleanDomain())
        val f37 = RandVar("F37", BooleanDomain())
        val f84 = RandVar("F84", BooleanDomain())
        val f96 = RandVar("F96", BooleanDomain())
        val f38 = RandVar("F38", BooleanDomain())
        val f39 = RandVar("F39", BooleanDomain())
        val f40 = RandVar("F40", BooleanDomain())
        val f42 = RandVar("F42", BooleanDomain())
        val f43 = RandVar("F43", BooleanDomain())
        val f45 = RandVar("F45", BooleanDomain())
        val f46 = RandVar("F46", BooleanDomain())
        val f47 = RandVar("F47", BooleanDomain())
        val f85 = RandVar("F85", BooleanDomain())
        val f48 = RandVar("F48", BooleanDomain())
        val f49 = RandVar("F49", BooleanDomain())
        val f50 = RandVar("F50", BooleanDomain())
        val f51 = RandVar("F51", BooleanDomain())
        val f83 = RandVar("F83", BooleanDomain())
        val f54 = RandVar("F54", BooleanDomain())
        val f57 = RandVar("F57", BooleanDomain())
        val f58 = RandVar("F58", BooleanDomain())
        val f59 = RandVar("F59", BooleanDomain())
        val f60 = RandVar("F60", BooleanDomain())
        val f61 = RandVar("F61", BooleanDomain())
        val f62 = RandVar("F62", BooleanDomain())
        val f63 = RandVar("F63", BooleanDomain())
        val f64 = RandVar("F64", BooleanDomain())
        val f65 = RandVar("F65", BooleanDomain())
        val f66 = RandVar("F66", BooleanDomain())
        val f67 = RandVar("F67", BooleanDomain())
        val f68 = RandVar("F68", BooleanDomain())
        val f69 = RandVar("F69", BooleanDomain())
        val f72 = RandVar("F72", BooleanDomain())
        val f86 = RandVar("F86", BooleanDomain())
        val f70 = RandVar("F70", BooleanDomain())
        val f71 = RandVar("F71", BooleanDomain())
        val f73 = RandVar("F73", BooleanDomain())
        val f74 = RandVar("F74", BooleanDomain())
        val f75 = RandVar("F75", BooleanDomain())
        val f76 = RandVar("F76", BooleanDomain())
        val f77 = RandVar("F77", BooleanDomain())
        val f79 = RandVar("F79", BooleanDomain())
        val f80 = RandVar("F80", BooleanDomain())
        val f81 = RandVar("F81", BooleanDomain())
        val f82 = RandVar("F82", BooleanDomain())
        val f87 = RandVar("F87", BooleanDomain())
        val f88 = RandVar("F88", BooleanDomain())
        val f89 = RandVar("F89", BooleanDomain())
        val f91 = RandVar("F91", BooleanDomain())
        val f93 = RandVar("F93", BooleanDomain())
        val f94 = RandVar("F94", BooleanDomain())
        val f95 = RandVar("F95", BooleanDomain())
        val f99 = RandVar("F99", BooleanDomain())
        val f100 = RandVar("F100", BooleanDomain())
        val f105 = RandVar("F105", BooleanDomain())
        val f101 = RandVar("F101", BooleanDomain())
        val f102 = RandVar("F102", BooleanDomain())
        val f103 = RandVar("F103", BooleanDomain())
        val f104 = RandVar("F104", BooleanDomain())
        val f106 = RandVar("F106", BooleanDomain())
        val f107 = RandVar("F107", BooleanDomain())
        val f108 = RandVar("F108", BooleanDomain())


        /**
         * [FullCPTNode]s declaration
         */
        val fault_node = FullCPTNode(fault, doubleArrayOf(0.5, 0.5))

        val f1_node = FullCPTNode(f1, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f97_node = FullCPTNode(f97, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f2_node = FullCPTNode(f2, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5),
                fault_node
        )

        val f78_node = FullCPTNode(f78, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5),
                fault_node
        )

        val f3_node = FullCPTNode(f3, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f78_node
        )

        val f4_node = FullCPTNode(f4, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5),
                fault_node
        )

        val f5_node = FullCPTNode(f5, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f2_node
        )

        val f53_node = FullCPTNode(f53, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f6_node = FullCPTNode(f6, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f53_node
        )

        val f7_node = FullCPTNode(f7, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f56_node = FullCPTNode(f56, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f8_node = FullCPTNode(f8, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f53_node, f56_node
        )

        val f9_node = FullCPTNode(f9, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f10_node = FullCPTNode(f10, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f55_node = FullCPTNode(f55, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f52_node = FullCPTNode(f52, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f55_node
        )

        val f11_node = FullCPTNode(f11, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f52_node
        )

        val f12_node = FullCPTNode(f12, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f13_node = FullCPTNode(f13, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f8_node
        )

        val f14_node = FullCPTNode(f14, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f15_node = FullCPTNode(f15, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f16_node = FullCPTNode(f16, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f15_node
        )

        val f17_node = FullCPTNode(f17, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f18_node = FullCPTNode(f18, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f17_node
        )

        val f19_node = FullCPTNode(f19, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f41_node = FullCPTNode(f41, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f44_node = FullCPTNode(f44, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f20_node = FullCPTNode(f20, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f41_node, f44_node
        )

        val f90_node = FullCPTNode(f90, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f21_node = FullCPTNode(f21, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f90_node
        )

        val f22_node = FullCPTNode(f22, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f21_node
        )

        val f23_node = FullCPTNode(f23, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f24_node = FullCPTNode(f24, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f25_node = FullCPTNode(f25, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f26_node = FullCPTNode(f26, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f27_node = FullCPTNode(f27, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f28_node = FullCPTNode(f28, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f92_node = FullCPTNode(f92, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f29_node = FullCPTNode(f29, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                f92_node
        )

        val f98_node = FullCPTNode(f98, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5),
                fault_node
        )

        val f30_node = FullCPTNode(f30, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f98_node
        )

        val f31_node =  FullCPTNode(f31, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f20_node, f41_node, f44_node)

        val f32_node = FullCPTNode(f32, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f56_node
        )

        val f33_node = FullCPTNode(f33, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f34_node = FullCPTNode(f34, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f35_node = FullCPTNode(f35, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f36_node = FullCPTNode(f36, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f37_node = FullCPTNode(f37, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f84_node = FullCPTNode(f84, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f20_node, f41_node
        )

        val f96_node = FullCPTNode(f96, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f41_node, f84_node
        )

        val f38_node = FullCPTNode(f38, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f41_node, f96_node
        )

        val f39_node =  FullCPTNode(f39, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69),
                fault_node, f96_node, f84_node, f41_node)

        val f40_node = FullCPTNode(f40, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f41_node, f96_node
        )

        val f42_node = FullCPTNode(f42, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5),
                fault_node
        )

        val f43_node = FullCPTNode(f43, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f45_node = FullCPTNode(f45, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f46_node = FullCPTNode(f46, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f47_node = FullCPTNode(f42, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f44_node
        )

        val f85_node = FullCPTNode(f85, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f20_node, f44_node
        )

        val f48_node = FullCPTNode(f48, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f44_node, f85_node
        )

        val f49_node = FullCPTNode(f49, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f44_node
        )

        val f50_node = FullCPTNode(f50, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f44_node
        )

        val f51_node = FullCPTNode(f51, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f83_node = FullCPTNode(f83, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f41_node, f84_node
        )

        val f54_node = FullCPTNode(f54, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f83_node
        )

        val f57_node = FullCPTNode(f57, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f58_node = FullCPTNode(f58, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f59_node = FullCPTNode(f59, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f60_node = FullCPTNode(f60, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f61_node = FullCPTNode(f54, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f53_node
        )

        val f62_node = FullCPTNode(f62, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f61_node
        )

        val f63_node = FullCPTNode(f63, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f64_node = FullCPTNode(f64, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f65_node = FullCPTNode(f65, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f66_node = FullCPTNode(f66, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f61_node
        )

        val f67_node = FullCPTNode(f67, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f68_node = FullCPTNode(f68, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f69_node = FullCPTNode(f69, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f72_node = FullCPTNode(f72, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f86_node =  FullCPTNode(f86, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                f20_node, f85_node, f84_node, f72_node)

        val f70_node = FullCPTNode(f70, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f72_node, f86_node
        )

        val f71_node = FullCPTNode(f71, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f72_node
        )

        val f73_node = FullCPTNode(f73, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f74_node = FullCPTNode(f74, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f30_node, f98_node
        )

        val f75_node = FullCPTNode(f75, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f76_node = FullCPTNode(f76, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f77_node = FullCPTNode(f77, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f79_node = FullCPTNode(f79, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f80_node = FullCPTNode(f80, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f81_node = FullCPTNode(f74, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f72_node, f86_node
        )

        val f82_node = FullCPTNode(f82, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f44_node, f85_node
        )

        val f87_node = FullCPTNode(f87, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f31_node, f41_node
        )

        val f88_node = FullCPTNode(f88, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f31_node, f44_node
        )

        val f89_node = FullCPTNode(f89, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.97, 0.03,
                0.31, 0.69,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03,
                0.31, 0.69,
                0.97, 0.03),
                fault_node, f88_node, f87_node, f31_node, f72_node
        )

        val f91_node = FullCPTNode(f91, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        val f93_node = FullCPTNode(f93, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.75, 0.25,
                0.63, 0.37,
                0.75, 0.25,
                0.63, 0.37,
                0.75, 0.25,
                0.63, 0.37),
                fault_node, f83_node, f82_node
        )

        val f94_node = FullCPTNode(f94, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.75, 0.25,
                0.63, 0.37,
                0.75, 0.25,
                0.63, 0.37,
                0.75, 0.25,
                0.63, 0.37),
                fault_node, f41_node, f96_node
        )

        val f95_node = FullCPTNode(f95, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.75, 0.25,
                0.63, 0.37),
                fault_node, f72_node
        )

        val f99_node = FullCPTNode(f99, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.75, 0.25,
                0.63, 0.37),
                fault_node, f90_node
        )

        val f100_node = FullCPTNode(f100, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.75, 0.25,
                0.63, 0.37),
                fault_node, f4_node
        )

        val f105_node = FullCPTNode(f105, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5),
                fault_node
        )

        val f101_node = FullCPTNode(f101, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.75, 0.25,
                0.63, 0.37),
                fault_node, f105_node
        )

        val f102_node = FullCPTNode(f102, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5,
                0.75, 0.25,
                0.63, 0.37),
                fault_node, f105_node
        )

        val f103_node = FullCPTNode(f103, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5),
                fault_node
        )

        val f104_node = FullCPTNode(f104, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5),
                fault_node
        )

        val f106_node = FullCPTNode(f106, doubleArrayOf(
                0.95, 0.05,
                0.5, 0.5),
                fault_node
        )

        val f107_node = FullCPTNode(f107, doubleArrayOf(
                0.97, 0.03,
                0.31, 0.69,
                0.75, 0.25,
                0.63, 0.37,
                0.75, 0.25,
                0.63, 0.37,
                0.75, 0.25,
                0.63, 0.37),
                fault_node, f41_node, f44_node
        )

        val f108_node = FullCPTNode(f108, doubleArrayOf(
                0.75, 0.25,
                0.63, 0.37),
                fault_node
        )

        return BayesNet(fault_node)
    }



