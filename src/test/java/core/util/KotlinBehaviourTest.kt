package core.util

import core.file.xml.XMLManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.w3c.dom.Document
import org.w3c.dom.Element

class KotlinBehaviourTest {

    @Test
    fun testMapsAddition() {
        val map1 = mapOf("1" to 42, "2" to 53)
        val map2 = mapOf("1" to 1, "2" to 666)
        val map = map1 + map2

        Assertions.assertEquals(1, map["1"])
        Assertions.assertEquals(666, map["2"])
    }

    @Test()
    fun testDoubleBangBehaviour() {
        val doc:Document? = XMLUtils.createDocument("<example><node val=\"42\" /></example>")
        val root = doc!!.documentElement
        val ele = root!!.getElementsByTagName("missing").item(0) as Element?

        assertThrows(NullPointerException::class.java) {
            ele!!.getElementsByTagName("sub-missing").item(0) as Element?
        }
    }

    @Test
    fun testQuestionMarkBehaviour() {
        val doc:Document? = XMLUtils.createDocument("<example><node val=\"42\" /></example>")
        val root = doc?.documentElement
        val ele = root?.getElementsByTagName("missing")?.item(0) as Element?

        assertNull(ele?.getElementsByTagName("sub-missing")?.item(0) as Element?)
    }

    @Test
    fun testReturnValueFirstChild() {
        val doc:Document? = XMLUtils.createDocument("<example><node>42</node></example>")
        val root = doc?.documentElement
        val ele = root?.getElementsByTagName("node")?.item(0) as Element?

        assertEquals("42", XMLManager.getFirstChildNodeValue(ele))
    }

    @Test
    fun testNotNullNodeListHint() {
        val doc:Document = XMLUtils.createDocument("<example><node>42</node></example>")
        val root = doc.documentElement

        // .item does not throw an NPE on returned NodeList when tag not present.
        val ele = root.getElementsByTagName("missing").item(0) as Element?

        assertNull(ele)
    }
}