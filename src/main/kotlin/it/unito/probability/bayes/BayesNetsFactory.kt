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

    fun getSachsNet(): BayesianNetwork{
        /**
         * Number of nodes: 11
         * Number of arcs: 17
         */

        /**
         * [RandomVariable]s declaration
         */
        val akt = RandVar("AKT", FiniteIntegerDomain())
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
                0.02, 0.98,
                0.98, 0.02),
                pKC_node
        )

        val raf_node = FullCPTNode(raf, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
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

        val akt_node = FullCPTNode(erk, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                erk_node, pKA_node
        )

        val jnk_node = FullCPTNode(jnk, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                pKA_node, pKC_node
        )

        val p38_node = FullCPTNode(p38, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02,
                0.98, 0.02,
                0.02, 0.98),
                pKA_node, pKC_node
        )

        val pIP3_node = FullCPTNode(pIP3, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02),
                plcg_node
        )

        val pIP2_node = FullCPTNode(pIP2, doubleArrayOf(
                0.02, 0.98,
                0.98, 0.02,
                0.98, 0.02,
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
                0.25, 0.75,
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
                0.3, 0.7,
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
                0.25, 0.75,
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
                0.5, 0.5),
                compPlFcst_node
        )

        val insChange_node = FullCPTNode(insChange, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5,
                0.35, 0.65,
                0.08, 0.92),
                loLevMoistAd_node, compPlFcst_node
        )

        val mountainFcst_node = FullCPTNode(mountainFcst, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                insInMt_node
        )

        val scenario_node = FullCPTNode(scenario, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                date_node
        )

        val scenRelAMCIN_node = FullCPTNode(scenRelAMCIN, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
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
                0.5, 0.5),
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
                0.5, 0.5),
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
                0.5, 0.5,
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
                0.5, 0.5),
                scenario_node
        )

        val lowLLapse_node = FullCPTNode(lowLLapse, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val meanRH_node = FullCPTNode(meanRH, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val midLLapse_node = FullCPTNode(midLLapse, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val mvmtFeatures_node = FullCPTNode(mvmtFeatures, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val rHRatio_node = FullCPTNode(rHRatio, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val sfcWndShfDis_node = FullCPTNode(sfcWndShfDis, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val synForcng_node = FullCPTNode(synForcng, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val tempDis_node = FullCPTNode(tempDis, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val windAloft_node = FullCPTNode(windAloft, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val windFieldMt_node = FullCPTNode(windFieldMt, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
                scenario_node
        )

        val windFieldPln_node = FullCPTNode(windFieldPln, doubleArrayOf(
                0.9, 0.1,
                0.5, 0.5),
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
        return BayesNet()
    }



