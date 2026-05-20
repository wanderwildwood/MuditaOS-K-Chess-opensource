package com.mudita.chess.gameplay.fixtures

internal val whiteParticipantWonPgn = """
    [Event "?"]
    [Site "?"]
    [Date "????.??.??"]
    [Round "?"]
    [White "?"]
    [Black "?"]
    [Result "1-0"]

    1. e4 e5 2. Nf3 Nc6 3. Nc3 g6 4. d4 exd4 5. Nd5 Bg7 6. Bg5 Nge7 7. Nxd4 Bxd4 8.
    Qxd4 Nxd4 9. Nf6+ Kf8 10. Bh6# 1-0
""".trimIndent()

internal val blackParticipantWonPgn = """
    [Event "?"]
    [Site "?"]
    [Date "????.??.??"]
    [Round "?"]
    [White "?"]
    [Black "?"]
    [Result "0-1"]
    [Link "https://www.chess.com/analysis?tab=analysis"]
    
    1. e4 d5 2. Ke2 dxe4 3. Ke3 c6 4. f3 h6 5. d3 g6 6. Bd2 f6 7. Qe2 e6 8. Na3 b6
    9. g3 Rh7 10. Bh3 a6 11. Rf1 Rg7 12. Rf2 Raa7 13. Bf1 Rab7 14. b3 b5 15. g4 Qb6+
    16. Kf4 g5+ 17. Kg3 h5 18. Kh3 Rh7 19. gxh5 Rxh5+ 20. Kg3 f5 21. Bg2 f4+ 22. Kg4
    e5+ 23. Kxh5 c5 24. h3 Rh7+ 25. Kxg5 Qf6# 0-1
""".trimIndent()

internal val insufficientMaterialDrawPgn = """
    [Event "?"]
    [Site "?"]
    [Date "????.??.??"]
    [Round "?"]
    [White "?"]
    [Black "?"]
    [Result "1/2-1/2"]
    [Link "https://www.chess.com/analysis?tab=analysis"]
    
    1. b4 a5 2. bxa5 b5 3. c4 bxc4 4. d4 c5 5. dxc5 d5 6. e4 dxe4 7. f4 e5 8. fxe5
    g5 9. h4 gxh4 10. g3 hxg3 11. e6 g2 12. exf7+ Ke7 13. fxg8=Q gxf1=Q+ 14. Kxf1 h6
    15. Rxh6 Rxh6 16. Nh3 Rxh3 17. Qxf8+ Ke6 18. Qfxd8 c3 19. Q1d3 Rxd3 20. Qxc8+
    Ke7 21. Qxb8 c2 22. Qxa8 cxb1=Q 23. Qa6 Qxc1+ 24. Kf2 Qb1 25. Qxd3 e3+ 26. Kg2
    Ke8 27. Qd2 exd2 28. c6 Qb3 29. axb3 d1=Q 30. Rb1 Qxb3 31. Rb2 Qxb2+ 32. Kg3 Qb6
    33. Kg2 Qxc6+ 34. Kf2 Qb6+ 35. axb6 Kd8 36. b7 Kd7 37. b8=N+ 1/2-1/2
""".trimIndent()

internal val after1RoundPgn = """
    [Event "?"]
    [Site "?"]
    [Date "????.??.??"]
    [Round "?"]
    [White "?"]
    [Black "?"]
    [Result "*"]
    [Link "https://www.chess.com/analysis?tab=analysis"]
    
    1. e4 e5 *
""".trimIndent()

internal val after2RoundsPgn = """
    [Event "?"]
    [Site "?"]
    [Date "????.??.??"]
    [Round "?"]
    [White "?"]
    [Black "?"]
    [Result "*"]
    [Link "https://www.chess.com/analysis?tab=analysis"]
    
    1. e4 e5 2. Nf3 f6 *
""".trimIndent()

internal val afterHalfRoundPgn = """
    [Event "?"]
    [Site "?"]
    [Date "????.??.??"]
    [Round "?"]
    [White "?"]
    [Black "?"]
    [Result "*"]
    [Link "https://www.chess.com/analysis?tab=analysis"]
    
    1. d4 *
""".trimIndent()

internal val blackInCheckPgn = """
    [Event "?"]
    [Site "?"]
    [Date "????.??.??"]
    [Round "?"]
    [White "?"]
    [Black "?"]
    [Result "*"]
    [Link "https://www.chess.com/analysis?tab=analysis"]
    
    1. e4 d5 2. Qg4 c6 3. Qd7+ *
""".trimIndent()