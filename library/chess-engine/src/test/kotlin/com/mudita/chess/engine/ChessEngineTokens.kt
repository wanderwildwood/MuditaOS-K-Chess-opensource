package com.mudita.chess.engine

object ChessEngineTokens {
    const val ENGINE_INTRO = "Fairy-Stockfish 14 by Fabian Fichter"

    val UCI_RESPONSE = """
        id name Fairy-Stockfish 14
        id author Fabian Fichter
        
        option name Protocol type combo default uci var uci var usi var ucci var ucicyclone var xboard
        option name Debug Log File type string default 
        option name Threads type spin default 1 min 1 max 512
        option name Hash type spin default 16 min 1 max 33554432
        option name Clear Hash type button
        option name Ponder type check default false
        option name MultiPV type spin default 1 min 1 max 500
        option name Skill Level type spin default 20 min -20 max 20
        option name Move Overhead type spin default 10 min 0 max 5000
        option name Slow Mover type spin default 100 min 10 max 1000
        option name nodestime type spin default 0 min 0 max 10000
        option name UCI_Chess960 type check default false
        option name UCI_Variant type combo default chess var 3check var 5check var ai-wok var almost var amazon var antichess var armageddon var asean var ataxx var atomic var breakthrough var bughouse var cambodian var chaturanga var chess var chessgi var chigorin var clobber var codrus var coregal var crazyhouse var dobutsu var euroshogi var extinction var fairy var fischerandom var gardner var giveaway var gorogoro var grasshopper var hoppelpoppel var horde var judkins var karouk var kinglet var kingofthehill var knightmate var koedem var kyotoshogi var loop var losalamos var losers var makpong var makruk var micro var mini var minishogi var minixiangqi var newzealand var nightrider var nocastle var nocheckatomic var normal var placement var pocketknight var racingkings var seirawan var shatar var shatranj var shouse var sittuyin var suicide var threekings var torishogi
        option name UCI_AnalyseMode type check default false
        option name UCI_LimitStrength type check default false
        option name UCI_Elo type spin default 1350 min 500 max 2850
        option name UCI_ShowWDL type check default false
        option name SyzygyPath type string default <empty>
        option name SyzygyProbeDepth type spin default 1 min 1 max 100
        option name Syzygy50MoveRule type check default true
        option name SyzygyProbeLimit type spin default 7 min 0 max 7
        option name Use NNUE type check default true
        option name EvalFile type string default <empty>
        option name TsumeMode type check default false
        option name VariantPath type string default <empty>
        uciok
    """.trimIndent().split("\n")

    const val READY_OK = "readyok"

    val SEARCH_RESPONSE = """
        info depth 1 seldepth 1 multipv 1 score cp 69 nodes 175 nps 87500 tbhits 0 time 2 pv e2e4
        info depth 2 seldepth 2 multipv 1 score cp 69 nodes 392 nps 196000 tbhits 0 time 2 pv e2e4 e7e5
        info depth 3 seldepth 3 multipv 1 score cp 69 nodes 591 nps 197000 tbhits 0 time 3 pv e2e4 e7e5 g1f3
        info depth 4 seldepth 4 multipv 1 score cp 69 nodes 913 nps 304333 tbhits 0 time 3 pv e2e4 e7e5 g1f3 b8c6
        info depth 5 seldepth 5 multipv 1 score cp 69 nodes 1267 nps 316750 tbhits 0 time 4 pv e2e4 e7e5 g1f3 b8c6 f1b5
        info depth 6 seldepth 6 multipv 1 score cp 69 nodes 1848 nps 308000 tbhits 0 time 6 pv e2e4 e7e5 g1f3 b8c6 f1b5 a7a6
        info depth 7 seldepth 7 multipv 1 score cp 69 nodes 3104 nps 344888 tbhits 0 time 9 pv e2e4 e7e5 g1f3 b8c6 f1b5 a7a6 b5c6
        info depth 8 seldepth 8 multipv 1 score cp 69 nodes 5269 nps 351266 tbhits 0 time 15 pv e2e4 e7e5 g1f3 b8c6 f1b5 a7a6 b5c6 d7c6
        info depth 9 seldepth 11 multipv 1 score cp 60 nodes 11222 nps 340060 tbhits 0 time 33 pv e2e4 e7e5 g1f3 b8c6 f1b5 g8f6 e1g1 f8c5
        info depth 10 seldepth 12 multipv 1 score cp 69 nodes 24163 nps 287654 tbhits 0 time 84 pv e2e4 e7e5 g1f3 b8c6 f1b5 g8f6 e1g1 f8c5 b5c6 d7c6
    """.trimIndent().split("\n")

    val BEST_MOVE_RESPONSE = SEARCH_RESPONSE + "bestmove e2e3 ponder g8f6"
}
