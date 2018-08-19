package net.thesilkminer.skl.interpreter.skd.tools.parser

import net.thesilkminer.skl.interpreter.skd.api.structure.Database
import net.thesilkminer.skl.interpreter.skd.api.tools.InvalidDatabaseException
import net.thesilkminer.skl.interpreter.skd.api.tools.Support
import net.thesilkminer.skl.interpreter.skd.rethrowAs

class Parser(override val support: Support) : net.thesilkminer.skl.interpreter.skd.api.tools.Parser {

    companion object {
        private val l = L(this)
    }

    private val database: Database by lazy { this.parse0() }

    override fun parse(): Database = this.database

    private fun parse0() : Database {
        return try {
            this.parseImpl()
        } catch (e: Exception) {
            l.e("FATAL! Exception {} has been thrown - unable to continue parsing", e)
            e rethrowAs ::InvalidDatabaseException
        }
    }

    private fun parseImpl(): Database {
        l.i("Database parsing process started")
        l.d("Using support {}, parser class is {}", this.support, this::class.java)
        l.t("Loading entirety of support into memory: deferring to loader {}", Loader::class.java)
        val lines = Loader(this.support).load().toMutableList()
        val declarator = Declarator(lines)
        declarator.parse()
        TODO()
    }

    override fun loadIntoAny(any: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
