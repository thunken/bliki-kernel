local u = mw.ustring.char

-- UTF-8 encoded strings for some commonly-used diacritics
local GRAVE     = u(0x0300)
local ACUTE     = u(0x0301)
local CIRC      = u(0x0302)
local TILDE     = u(0x0303)
local MACRON    = u(0x0304)
local BREVE     = u(0x0306)
local DOTABOVE  = u(0x0307)
local DIAER     = u(0x0308)
local CARON     = u(0x030C)
local DGRAVE    = u(0x030F)
local INVBREVE  = u(0x0311)
local DOTBELOW  = u(0x0323)
local RINGBELOW = u(0x0325)
local CEDILLA   = u(0x0327)

local m = {}

m["aa"] = {
    names = {"Afar", "Qafar"},
    type = "regular",
    scripts = {"Latn"},
    family = "cus",
}

m["ab"] = {
    names = {"Abkhaz", "Abkhazian", "Abxazo"},
    type = "regular",
    scripts = {"Cyrl"},
    family = "cau-nwc",
    translit_module = "ab-translit",
}

m["ae"] = {
    names = {"Avestan", "Zend", "Old Bactrian"},
    type = "regular",
    scripts = {"Avst", "Gujr"},
    family = "ira",
    translit_module = "Avst-translit",
}

m["af"] = {
    names = {"Afrikaans"},
    type = "regular",
    scripts = {"Latn", "Arab"},
    family = "gmw",
    ancestors = {"nl"},
    sort_key = {
        from = {"[äáâà]", "[ëéêè]", "[ïíîì]", "[öóôò]", "[üúûù]", "[ÿýŷỳ]", "^-", "'"},
        to   = {"a"	 , "e"	, "i"	, "o"	, "u"  , "y" }} ,
}

m["ak"] = {
    names = {"Akan", "Twi-Fante"},
    type = "regular",
    scripts = {"Latn"},
    family = "alv-kwa",
}

m["am"] = {
    names = {"Amharic"},
    type = "regular",
    scripts = {"Ethi"},
    family = "sem-eth",
    translit_module = "Ethi-translit",
}

m["an"] = {
    names = {"Aragonese"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
}

m["ar"] = {
    names = {"Arabic", "Modern Standard Arabic", "Standard Arabic", "Literary Arabic", "Classical Arabic"},
    type = "regular",
    scripts = {"Arab"},
    family = "sem-arb",
    entry_name = {
        from = {u(0x0671), u(0x064B), u(0x064C), u(0x064D), u(0x064E), u(0x064F), u(0x0650), u(0x0651), u(0x0652), u(0x0670), u(0x0640)},
        to   = {u(0x0627)}},
    translit_module = "ar-translit",
}

m["as"] = {
    names = {"Assamese"},
    type = "regular",
    scripts = {"Beng"},
    family = "inc",
}

m["av"] = {
    names = {"Avar", "Avaric"},
    type = "regular",
    scripts = {"Cyrl"},
    family = "cau-nec",
    translit_module = "av-translit",
}

m["ay"] = {
    names = {"Aymara", "Southern Aymara", "Central Aymara"},
    type = "regular",
    scripts = {"Latn"},
    family = "sai-aym",
}

m["az"] = {
    names = {"Azeri", "Azerbaijani", "Azari", "Azeri Turkic", "Azerbaijani Turkic", "North Azerbaijani", "South Azerbaijani"},
    type = "regular",
    scripts = {"Latn", "Cyrl", "fa-Arab"},
    family = "trk",
}

m["ba"] = {
    names = {"Bashkir"},
    type = "regular",
    scripts = {"Cyrl"},
    family = "trk",
    translit_module = "ba-translit",
}

m["be"] = {
    names = {"Belarusian", "Belorussian", "Belarusan", "Bielorussian", "Byelorussian", "Belarussian", "White Russian"},
    type = "regular",
    scripts = {"Cyrl"},
    family = "zle",
    ancestors = {"orv"},
    translit_module = "be-translit",
    sort_key = {
        from = {"Ё", "ё"},
        to   = {"Е" , "е"}},
    entry_name = {
        from = {GRAVE, ACUTE},
        to   = {}} ,
}

m["bg"] = {
    names = {"Bulgarian"},
    type = "regular",
    scripts = {"Cyrl"},
    family = "zls",
    translit_module = "bg-translit",
    entry_name = {
        from = {"Ѐ", "ѐ", "Ѝ", "ѝ", GRAVE, ACUTE},
        to   = {"Е", "е", "И", "и"}} ,
}

m["bh"] = {
    names = {"Bihari"},
    type = "regular",
    scripts = {"Deva"},
    family = "inc",
}

m["bi"] = {
    names = {"Bislama"},
    type = "regular",
    scripts = {"Latn"},
    family = "crp",
    ancestors = {"en"},
}

m["bm"] = {
    names = {"Bambara", "Bamanankan"},
    type = "regular",
    scripts = {"Latn"},
    family = "dmn",
}

m["bn"] = {
    names = {"Bengali", "Bangla"},
    type = "regular",
    scripts = {"Beng"},
    family = "inc",
}

m["bo"] = {
    names = {"Tibetan"},
    type = "regular",
    scripts = {"Tibt"},
    family = "tbq",
    translit_module = "bo-translit",
}

m["br"] = {
    names = {"Breton"},
    type = "regular",
    scripts = {"Latn"},
    family = "cel-bry",
    ancestors = {"xbm"},
}

m["ca"] = {
    names = {"Catalan", "Valencian"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
    sort_key = {
        from = {"à", "[èé]", "[íï]", "[òó]", "[úü]", "ç", "l·l"},
        to   = {"a", "e"   , "i"   , "o"   , "u"   , "c", "ll" }} ,
}

m["ce"] = {
    names = {"Chechen"},
    type = "regular",
    scripts = {"Cyrl"},
    family = "cau-nec",
    translit_module = "ce-translit",
}

m["ch"] = {
    names = {"Chamorro", "Chamoru"},
    type = "regular",
    scripts = {"Latn"},
    family = "poz-sus",
}

m["co"] = {
    names = {"Corsican", "Corsu"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
}

m["cr"] = {
    names = {"Cree"},
    type = "regular",
    scripts = {"Cans", "Latn"},
    family = "alg",
}

m["cs"] = {
    names = {"Czech"},
    type = "regular",
    scripts = {"Latn"},
    family = "zlw",
    sort_key = {
        from = {"á", "é", "í", "ó", "[úů]", "ý"},
        to   = {"a", "e", "i", "o", "u"   , "y"}} ,
}

m["cu"] = {
    names = {"Old Church Slavonic", "Old Church Slavic"},
    type = "regular",
    scripts = {"Cyrs", "Glag"},
    family = "zls",
    translit_module = "Cyrs-Glag-translit",
    entry_name = {
        from = {u(0x0484)}, -- kamora
        to   = {}},
    sort_key = {
        from = {"оу", "є"},
        to   = {"у" , "е"}} ,
}

m["cv"] = {
    names = {"Chuvash"},
    type = "regular",
    scripts = {"Cyrl"},
    family = "trk",
    translit_module = "cv-translit",
}

m["cy"] = {
    names = {"Welsh"},
    type = "regular",
    scripts = {"Latn"},
    family = "cel-bry",
    ancestors = {"wlm"},
    sort_key = {
        from = {"[âáàä]", "[êéèë]", "[îíìï]", "[ôóòö]", "[ûúùü]", "[ŵẃẁẅ]", "[ŷýỳÿ]", "'"},
        to   = {"a"	 , "e"	 , "i"	 , "o"	 , "u"	 , "w"	 , "y"	 }} ,
}

m["da"] = {
    names = {"Danish"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmq",
}

m["de"] = {
    names = {"German", "High German", "New High German", "Deutsch"}, -- the last name is indeed also used in English
    type = "regular",
    scripts = {"Latn", "Latf"},
    family = "gmw",
    ancestors = {"gmh"},
    sort_key = {
        from = {"[äàáâå]", "[ëèéê]", "[ïìíî]", "[öòóô]", "[üùúû]", "ß" },
        to   = {"a"	  , "e"	 , "i"	 , "o"	 , "u"	 , "ss"}} ,
}

m["dv"] = {
    names = {"Dhivehi", "Divehi", "Mahal", "Mahl", "Maldivian"},
    type = "regular",
    scripts = {"Thaa"},
    family = "inc",
    translit_module = "dv-translit",
}

m["dz"] = {
    names = {"Dzongkha"},
    type = "regular",
    scripts = {"Tibt"},
    family = "tbq",
    translit_module = "bo-translit",
}

m["ee"] = {
    names = {"Ewe"},
    type = "regular",
    scripts = {"Latn"},
    family = "alv",
}

m["el"] = {
    names = {"Greek", "Modern Greek", "Neo-Hellenic"},
    type = "regular",
    scripts = {"Grek"},
    family = "grk",
    translit_module = "el-translit",
    sort_key = {  -- Keep this synchronized with grc, cpg
        from = {"[ᾳάᾴὰᾲᾶᾷἀᾀἄᾄἂᾂἆᾆἁᾁἅᾅἃᾃἇᾇ]", "[έὲἐἔἒἑἕἓ]", "[ῃήῄὴῂῆῇἠᾐἤᾔἢᾒἦᾖἡᾑἥᾕἣᾓἧᾗ]", "[ίὶῖἰἴἲἶἱἵἳἷϊΐῒῗ]", "[όὸὀὄὂὁὅὃ]", "[ύὺῦὐὔὒὖὑὕὓὗϋΰῢῧ]", "[ῳώῴὼῲῶῷὠᾠὤᾤὢᾢὦᾦὡᾡὥᾥὣᾣὧᾧ]", "ῥ", "ς"},
        to   = {"α"						, "ε"		 , "η"						, "ι"				, "ο"		 , "υ"				, "ω"						, "ρ", "σ"}} ,
}

m["en"] = {
    names = {"English", "Modern English", "New English", "Hawaiian Creole English", "Hawai'ian Creole English", "Hawaiian Creole", "Hawai'ian Creole", "Polari", "Yinglish"}, -- all but the first three are names and alt names of subsumed dialects which once had ISO codes
    type = "regular",
    scripts = {"Latn", "Shaw", "Dsrt"}, -- last two are rare but probably attested; entries in them might require community approval, but it's good for the script codes not to be orphans
    family = "gmw",
    ancestors = {"enm"},
    sort_key = {
        from = {"[äàáâåā]", "[ëèéêē]", "[ïìíîī]", "[öòóôō]", "[üùúûū]", "æ" , "œ" , "[çč]", "ñ", "'"},
        to   = {"a"       , "e"      , "i"      , "o"      , "u"      , "ae", "oe", "c"   , "n"}},
    wikimedia_codes = {"en", "simple"},
}

m["eo"] = {
    names = {"Esperanto"},
    type = "regular",
    scripts = {"Latn"},
    family = "art",
    sort_key = {
        from = {"[áà]", "[éè]", "[íì]", "[óò]", "[úù]", "[ĉ]", "[ĝ]", "[ĥ]", "[ĵ]", "[ŝ]", "[ŭ]"},
        to   = {"a"	   , "e"  , "i"  , "o"  , "u", "cĉ", "gĉ", "hĉ", "jĉ", "sĉ", "uĉ"}} ,
}

m["es"] = {
    names = {"Spanish", "Castilian"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
    sort_key = {
        from = {"á", "é", "í", "ó", "[úü]", "ç", "ñ"},
        to   = {"a", "e", "i", "o", "u"   , "c", "n"}} ,
}

m["et"] = {
    names = {"Estonian"},
    type = "regular",
    scripts = {"Latn"},
    family = "fiu-fin",
    ancestors = {"fiu-fin-pro"},
}

m["eu"] = {
    names = {"Basque", "Euskara"},
    type = "regular",
    scripts = {"Latn"},
    family = "euq",
}

m["fa"] = {
    names = {"Persian", "Farsi", "New Persian", "Modern Persian", "Western Persian", "Iranian Persian", "Eastern Persian", "Dari"},
    type = "regular",
    scripts = {"fa-Arab"},
    family = "ira",
    entry_name = {
        from = {u(0x064E), u(0x064F), u(0x0650), u(0x0651), u(0x0652)},
        to   = {}} ,
}

m["ff"] = {
    names = {"Fula", "Adamawa Fulfulde", "Bagirmi Fulfulde", "Borgu Fulfulde", "Central-Eastern Niger Fulfulde", "Fulani", "Fulfulde", "Maasina Fulfulde", "Nigerian Fulfulde", "Pular", "Pulaar", "Western Niger Fulfulde"}, -- Maasina, etc are dialects, subsumed into this code
    type = "regular",
    scripts = {"Latn"},
    family = "alv-sng",
}

m["fi"] = {
    names = {"Finnish", "Suomi"},
    type = "regular",
    scripts = {"Latn"},
    family = "fiu-fin",
    ancestors = {"fiu-fin-pro"},
    entry_name = {
        from = {"ˣ"},  -- Used to indicate gemination of the next consonant
        to   = {}},
    sort_key = {
        from = {"[áàâã]", "[éèêẽ]", "[íìîĩ]", "[óòôõ]", "[úùûũ]", "[ýỳŷüű]", "[øõő]", "æ" , "œ" , "[čç]", "š", "ž", "ß" , "[':]"},
        to   = {"a"	 , "e"	 , "i"	 , "o"	 , "u"	 ,  "y"	 , "ö"	, "ae", "oe", "c"   , "s", "z", "ss"}} ,
}

m["fj"] = {
    names = {"Fijian"},
    type = "regular",
    scripts = {"Latn"},
    family = "poz-occ",
}

m["fo"] = {
    names = {"Faroese"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmq",
}

m["fr"] = {
    names = {"French", "Modern French"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
    ancestors = {"frm"},
    sort_key = {
        from = {"[áàâä]", "[éèêë]", "[íìîï]", "[óòôö]", "[úùûü]", "[ýỳŷÿ]", "ç", "æ" , "œ" , "'"},
        to   = {"a"	 , "e"	 , "i"	 , "o"	 , "u"	 , "y"	 , "c", "ae", "oe"}} ,
}

m["fy"] = {
    names = {"West Frisian", "Western Frisian", "Frisian"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmw-fri",
}

m["ga"] = {
    names = {"Irish", "Irish Gaelic"},
    type = "regular",
    scripts = {"Latn"},
    family = "cel-gae",
    ancestors = {"mga"},
    sort_key = {
        from = {"á", "é", "í", "ó", "ú", "ý", "ḃ" , "ċ" , "ḋ" , "ḟ" , "ġ" , "ṁ" , "ṗ" , "ṡ" , "ṫ" },
        to   = {"a", "e", "i", "o", "u", "y", "bh", "ch", "dh", "fh", "gh", "mh", "ph", "sh", "th"}} ,
}

m["gd"] = {
    names = {"Scottish Gaelic", "Gàidhlig", "Highland Gaelic", "Scots Gaelic", "Scottish"},
    type = "regular",
    scripts = {"Latn"},
    family = "cel-gae",
    ancestors = {"mga"},
    sort_key = {
        from = {"[áà]", "[éè]", "[íì]", "[óò]", "[úù]", "[ýỳ]"},
        to   = {"a"   , "e"   , "i"   , "o"   , "u"   , "y"   }} ,
}

m["gl"] = {
    names = {"Galician"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
}

m["gn"] = {
    names = {"Guaraní"},
    type = "regular",
    scripts = {"Latn"},
    family = "tup",
}

m["gu"] = {
    names = {"Gujarati"},
    type = "regular",
    scripts = {"Gujr"},
    family = "inc",
}

m["gv"] = {
    names = {"Manx", "Manx Gaelic"},
    type = "regular",
    scripts = {"Latn"},
    family = "cel-gae",
    ancestors = {"mga"},
    sort_key = {
        from = {"ç", "-"},
        to   = {"c"}} ,
}

m["ha"] = {
    names = {"Hausa"},
    type = "regular",
    scripts = {"Latn", "Arab"},
    family = "cdc-wst",
}

m["he"] = {
    names = {"Hebrew", "Ivrit"},
    type = "regular",
    scripts = {"Hebr"},
    family = "sem-can",
    entry_name = {
        from = {"[" .. u(0x0591) .. "-" .. u(0x05BD) .. u(0x05BF) .. "-" .. u(0x05C5) .. u(0x05C7) .. "]"},
        to   = {}} ,
}

m["hi"] = {
    names = {"Hindi"},
    type = "regular",
    scripts = {"Deva"},
    family = "inc",
}

m["ho"] = {
    names = {"Hiri Motu", "Pidgin Motu", "Police Motu"},
    type = "regular",
    scripts = {"Latn"},
    family = "crp",
    ancestors = {"meu"},
}

m["ht"] = {
    names = {"Haitian Creole", "Creole", "Haitian", "Kreyòl"},
    type = "regular",
    scripts = {"Latn"},
    family = "crp",
}

m["hu"] = {
    names = {"Hungarian", "Magyar"},
    type = "regular",
    scripts = {"Latn"},
    family = "fiu-ugr",
    sort_key = {
        from = {"á", "é", "í", "ó", "ú", "ő", "ű"},
        to   = {"a", "e", "i", "o", "u", "ö", "ü"}} ,
}

m["hy"] = {
    names = {"Armenian", "Modern Armenian", "Eastern Armenian", "Western Armenian"},
    type = "regular",
    scripts = {"Armn"},
    family = "hyx",
    translit_module = "Armn-translit",
    sort_key = {
        from = {"ու", "և", "եւ"},
        to   = {"ւ", "եվ", "եվ"}},
    entry_name = {
        from = {"՞", "՜", "՛", "՟", "և", "<sup>յ</sup>", "ՙ", "̈", "յ̵"},
        to   = {"", "", "", "", "եւ", "յ", "", "", "յ"}} ,
}

m["hz"] = {
    names = {"Herero"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["ia"] = {
    names = {"Interlingua"},
    type = "regular",
    scripts = {"Latn"},
    family = "art",
}

m["id"] = {
    names = {"Indonesian"},
    type = "regular",
    scripts = {"Latn"},
    family = "poz-mly",
}

m["ie"] = {
    names = {"Interlingue", "Occidental"},
    type = "regular",
    scripts = {"Latn"},
    family = "art",
}

m["ig"] = {
    names = {"Igbo"},
    type = "regular",
    scripts = {"Latn"},
    family = "nic-bco",
}

m["ii"] = {
    names = {"Sichuan Yi"},
    type = "regular",
    scripts = {"Yiii"},
    family = "tbq",
}

m["ik"] = {
    names = {"Inupiak", "Inupiaq", "Iñupiaq", "Inupiatun"},
    type = "regular",
    scripts = {"Latn"},
    family = "esx-inu",
}

m["io"] = {
    names = {"Ido"},
    type = "regular",
    scripts = {"Latn"},
    family = "art",
}

m["is"] = {
    names = {"Icelandic"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmq",
    ancestors = {"non"},
}

m["it"] = {
    names = {"Italian"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
    sort_key = {
        from = {"[àáâäå]", "[èéêë]", "[ìíîï]", "[òóôö]", "[ùúûü]"},
        to   = {"a"	  , "e"	 , "i"	 , "o"	 , "u"	 }} ,
}

m["iu"] = {
    names = {"Inuktitut", "Eastern Canadian Inuktitut", "Eastern Canadian Inuit", "Western Canadian Inuktitut", "Western Canadian Inuit", "Western Canadian Inuktun", "Inuinnaq", "Inuinnaqtun", "Inuvialuk", "Inuvialuktun", "Nunavimmiutit", "Nunatsiavummiut", "Aivilimmiut", "Natsilingmiut", "Kivallirmiut", "Siglit", "Siglitun"},
    type = "regular",
    scripts = {"Cans", "Latn"},
    family = "esx-inu",
    translit_module = "iu-translit",
}

m["ja"] = {
    names = {"Japanese", "Modern Japanese", "Nipponese", "Nihongo"},
    type = "regular",
    scripts = {"Jpan", "Latn", "Hira"},
    family = "jpx",
}

m["jv"] = {
    names = {"Javanese"},
    type = "regular",
    scripts = {"Latn", "Java"},
    family = "poz-sus",
}

m["ka"] = {
    names = {"Georgian", "Kartvelian"},
    type = "regular",
    scripts = {"Geor", "Geok"},
    family = "ccs",
    translit_module = "Geor-translit",
    entry_name = {
        from = {"̂"},
        to   = {""}},
}

m["kg"] = {
    names = {"Kongo", "Kikongo", "Koongo", "Laari", "San Salvador Kongo", "Yombe"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["ki"] = {
    names = {"Kikuyu"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["kj"] = {
    names = {"Kwanyama", "Kuanyama", "Oshikwanyama"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["kk"] = {
    names = {"Kazakh"},
    type = "regular",
    scripts = {"Cyrl", "Latn", "Arab", "kk-Arab"},
    family = "trk",
    translit_module = "kk-translit",
}

m["kl"] = {
    names = {"Greenlandic", "Kalaallisut"},
    type = "regular",
    scripts = {"Latn"},
    family = "esx-inu",
}

m["km"] = {
    names = {"Khmer", "Cambodian"},
    type = "regular",
    scripts = {"Khmr"},
    family = "mkh",
}

m["kn"] = {
    names = {"Kannada"},
    type = "regular",
    scripts = {"Knda"},
    family = "dra",
    translit_module = "kn-translit",
}

m["ko"] = {
    names = {"Korean", "Modern Korean"},
    type = "regular",
    scripts = {"Kore"},
    family = "qfa-kor",
    translit_module = "ko-translit",
}

m["kr"] = {
    names = {"Kanuri", "Kanembu", "Bilma Kanuri", "Central Kanuri", "Manga Kanuri", "Tumari Kanuri"},
    type = "regular",
    scripts = {"Latn"},
    family = "ssa",
}

m["ks"] = {
    names = {"Kashmiri"},
    type = "regular",
    scripts = {"ks-Arab", "Deva"},
    family = "iir-dar",
}

m["ku"] = {
    names = {"Kurdish"},
    type = "regular",
    scripts = {"Latn", "ku-Arab"},
    family = "ira",
}

m["kw"] = {
    names = {"Cornish"},
    type = "regular",
    scripts = {"Latn"},
    family = "cel-bry",
    ancestors = {"cnx"},
}

m["ky"] = {
    names = {"Kyrgyz", "Kirghiz", "Kirgiz"},
    type = "regular",
    scripts = {"Cyrl", "Latn", "Arab"},
    family = "trk",
    translit_module = "ky-translit",
}

m["la"] = {
    names = {"Latin"},
    type = "regular",
    scripts = {"Latn"},
    family = "itc",
    ancestors = {"itc-ola"},
    entry_name = {
        from = {"Ā", "ā", "Ē", "ē", "Ī", "ī", "Ō", "ō", "Ū", "ū", "Ȳ", "ȳ", "ë"},
        to   = {"A", "a", "E", "e", "I", "i", "O", "o", "U", "u", "Y", "y", "e"}} ,
}

m["lb"] = {
    names = {"Luxembourgish"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmw",
    ancestors = {"gmh"},
}

m["lg"] = {
    names = {"Luganda", "Ganda"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["li"] = {
    names = {"Limburgish", "Limburgan", "Limburgian", "Limburgic"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmw",
    ancestors = {"dum"},
}

m["ln"] = {
    names = {"Lingala"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["lo"] = {
    names = {"Lao", "Laotian"},
    type = "regular",
    scripts = {"Laoo"},
    family = "tai-swe",
    translit_module = "lo-translit",
}

m["lt"] = {
    names = {"Lithuanian"},
    type = "regular",
    scripts = {"Latn"},
    family = "bat",
    entry_name = {
        from = {"[áãà]", "[éẽè]", "[íĩì]", "[ýỹ]", "ñ", "[óõò]", "[úù]", ACUTE, GRAVE, TILDE},
        to   = {"a"    , "e"    , "i", "y"   , "n", "o"	   , "u"   }} ,
}

m["lu"] = {
    names = {"Luba-Katanga"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["lv"] = {
    names = {"Latvian", "Lettish", "Lett"},
    type = "regular",
    scripts = {"Latn"},
    family = "bat",
}

m["mg"] = {
    names = {"Malagasy", "Betsimisaraka Malagasy", "Betsimisaraka", "Northern Betsimisaraka Malagasy", "Northern Betsimisaraka", "Southern Betsimisaraka Malagasy", "Southern Betsimisaraka", "Bara Malagasy", "Bara", "Masikoro Malagasy", "Masikoro", "Antankarana", "Antankarana Malagasy", "Plateau Malagasy", "Sakalava", "Tandroy Malagasy", "Tandroy", "Tanosy", "Tanosy Malagasy", "Tesaka", "Tsimihety", "Tsimihety Malagasy"},
    type = "regular",
    scripts = {"Latn"},
    family = "poz-bre",
}

m["mh"] = {
    names = {"Marshallese"},
    type = "regular",
    scripts = {"Latn"},
    family = "poz-mic",
    sort_key = {
        from = {"ā" , "ļ" , "m̧" , "ņ" , "n̄"  , "o̧" , "ō"  , "ū" },
        to   = {"a~", "l~", "m~", "n~", "n~~", "o~", "o~~", "u~"}} ,
}

m["mi"] = {
    names = {"Maori", "Māori"},
    type = "regular",
    scripts = {"Latn"},
    family = "poz-pol",
}

m["mk"] = {
    names = {"Macedonian"},
    type = "regular",
    scripts = {"Cyrl"},
    family = "zls",
    translit_module = "mk-translit",
    entry_name = {
        from = {ACUTE},
        to   = {}},
}

m["ml"] = {
    names = {"Malayalam"},
    type = "regular",
    scripts = {"Mlym"},
    family = "dra",
    translit_module = "ml-translit",
}

m["mn"] = {
    names = {"Mongolian", "Khalkha Mongolian"},
    type = "regular",
    scripts = {"Cyrl", "Mong"},
    family = "xgn",
    translit_module = "mn-translit",
}

m["mr"] = {
    names = {"Marathi"},
    type = "regular",
    scripts = {"Deva"},
    family = "inc",
}

m["ms"] = {
    names = {"Malay"},
    type = "regular",
    scripts = {"Latn", "Arab"},
    family = "poz-mly",
}

m["mt"] = {
    names = {"Maltese"},
    type = "regular",
    scripts = {"Latn"},
    family = "sem-arb",
}

m["my"] = {
    names = {"Burmese", "Myanmar"},
    type = "regular",
    scripts = {"Mymr"},
    family = "tbq-brm",
    ancestors = {"obr"},
    translit_module = "my-translit",
}

m["na"] = {
    names = {"Nauruan", "Nauru"},
    type = "regular",
    scripts = {"Latn"},
    family = "poz-mic",
}

m["nb"] = {
    names = {"Norwegian Bokmål", "Bokmål"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmq",
    wikimedia_codes = {"no"},
}

m["nd"] = {
    names = {"Northern Ndebele", "North Ndebele"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["ne"] = {
    names = {"Nepali", "Nepalese"},
    type = "regular",
    scripts = {"Deva"},
    family = "inc",
}

m["ng"] = {
    names = {"Ndonga"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["nl"] = {
    names = {"Dutch", "Netherlandic", "Flemish"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmw",
    ancestors = {"dum"},
    sort_key = {
        from = {"[äáâå]", "[ëéê]", "[ïíî]", "[öóô]", "[üúû]", "ç", "ñ", "^-"},
        to   = {"a"	 , "e"	, "i"	, "o"	, "u"	, "c", "n"}} ,
}

m["nn"] = {
    names = {"Norwegian Nynorsk", "New Norwegian", "Nynorsk"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmq",
}

m["no"] = {
    names = {"Norwegian"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmq",
}

m["nr"] = {
    names = {"Southern Ndebele", "South Ndebele"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["nv"] = {
    names = {"Navajo"},
    type = "regular",
    scripts = {"nv-Latn"},
    family = "apa",
    sort_key = {
        from = {"[áą]", "[éę]", "[íį]", "[óǫ]", "ń", "^n([djlt])", "ł" , "[ʼ’']", ACUTE},
        to   = {"a"   , "e"   , "i"   , "o"   , "n", "ni%1"	  , "l"}} }  -- the copyright sign is used to guarantee that ł will always be sorted after all other words with l
m["ny"] = {
    names = {"Chichewa", "Chicheŵa", "Chinyanja", "Nyanja"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["oc"] = {
    names = {"Occitan", "Provençal", "Auvergnat", "Auvernhat", "Gascon", "Languedocien", "Lengadocian"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
    sort_key = {
        from = {"[àá]", "[èé]", "[íï]", "[òó]", "[úü]", "ç", "([lns])·h"},
        to   = {"a"   , "e"   , "i"   , "o"   , "u"   , "c", "%1h"	  }} ,
}

m["oj"] = {
    names = {"Ojibwe", "Chippewa", "Ojibway", "Ojibwemowin", "Southwestern Ojibwa"},
    type = "regular",
    scripts = {"Cans", "Latn"},
    family = "alg",
}

m["om"] = {
    names = {"Oromo", "Orma", "Borana-Arsi-Guji Oromo", "West Central Oromo"},
    type = "regular",
    scripts = {"Latn", "Ethi"},
    family = "cus",
}

m["or"] = {
    names = {"Oriya", "Odia", "Oorya"},
    type = "regular",
    scripts = {"Orya"},
    family = "inc",
}

m["os"] = {
    names = {"Ossetian", "Ossete", "Ossetic", "Digor", "Iron"},
    type = "regular",
    scripts = {"Cyrl", "Geor", "Latn"},
    family = "ira",
    translit_module = "os-translit",
    entry_name = {
        from = {GRAVE, ACUTE},
        to   = {}} ,
}

m["pa"] = {
    names = {"Punjabi", "Panjabi"},
    type = "regular",
    scripts = {"Guru", "Arab", "Deva"},
    family = "inc",
}

m["pi"] = {
    names = {"Pali"},
    type = "regular",
    scripts = {"Latn", "Deva", "Sinh", "Mymr", "Khmr", "Thai"},
    family = "inc",
    sort_key = {
        from = {"ā", "ī", "ū", "ḍ", "ḷ", "[ṁṃ]", "[ṇñṅ]", "ṭ"},
        to   = {"a", "i", "u", "d", "l", "m"   , "n"	, "t"}} ,
}

m["pl"] = {
    names = {"Polish"},
    type = "regular",
    scripts = {"Latn"},
    family = "zlw",
    sort_key = {
        from = {"[Ąą]", "[Ćć]", "[Ęę]", "[Łł]", "[Ńń]", "[Óó]", "[Śś]", "[Żż]", "[Źź]"},
        to   = {
            "a" .. u(0x10FFFF),
            "c" .. u(0x10FFFF),
            "e" .. u(0x10FFFF),
            "l" .. u(0x10FFFF),
            "n" .. u(0x10FFFF),
            "o" .. u(0x10FFFF),
            "s" .. u(0x10FFFF),
            "z" .. u(0x10FFFF),
            "z" .. u(0x10FFFE)}} ,
}

m["ps"] = {
    names = {"Pashto", "Pashtun", "Pushto", "Pashtu", "Central Pashto", "Northern Pashto", "Southern Pashto", "Pukhto", "Pakhto", "Pakkhto", "Afghani"},
    type = "regular",
    scripts = {"ps-Arab"},
    family = "ira",
}

m["pt"] = {
    names = {"Portuguese", "Modern Portuguese"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
    sort_key = {
        from = {"[àãáâä]", "[èẽéêë]", "[ìĩíï]", "[òóôõö]", "[üúùũ]", "ç", "ñ"},
        to   = {"a"	  , "e"	  , "i"	 , "o"	  , "u"	 , "c", "n"}} ,
}

m["qu"] = {
    names = {"Quechua"},
    type = "regular",
    scripts = {"Latn"},
    family = "qwe",
}

m["rm"] = {
    names = {"Romansch", "Romansh", "Rumantsch", "Romanche"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
}

m["rn"] = {
    names = {"Kirundi"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["ro"] = {
    names = {"Romanian", "Daco-Romanian", "Roumanian", "Rumanian"},
    type = "regular",
    scripts = {"Latn", "Cyrl"},
    family = "roa",
}

m["ru"] = {
    names = {"Russian"},
    type = "regular",
    scripts = {"Cyrl"},
    family = "zle",
    ancestors = {"orv"},
    translit_module = "ru-translit",
    sort_key = {
        from = {"ё"},
        to   = {"е" .. mw.ustring.char(0x10FFFF)}},
    entry_name = {
        from = {GRAVE, ACUTE},
        to   = {}} ,
}

m["rw"] = {
    names = {"Kinyarwanda"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["sa"] = {
    names = {"Sanskrit"},
    type = "regular",
    scripts = {"Deva", "Beng", "Brah", "Gran", "Gujr", "Guru", "Khar", "Knda", "Mlym", "Mymr", "Orya", "Shrd", "Sinh", "Taml", "Telu", "Thai", "Tibt"},
    family = "inc",
    ancestors = {"inc-pro"},
    translit_module = "sa-translit",
}

m["sc"] = {
    names = {"Sardinian", "Campidanese", "Logudorese", "Nuorese"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
}

m["sd"] = {
    names = {"Sindhi"},
    type = "regular",
    scripts = {"sd-Arab"},
    family = "inc",
}

m["se"] = {
    names = {"Northern Sami", "North Sami", "Northern Saami", "North Saami"},
    type = "regular",
    scripts = {"Latn"},
    family = "smi",
    entry_name = {
        from = {"([đflmnŋrsšŧv])'%1"},
        to   = {"%1%1"} },
}

m["sg"] = {
    names = {"Sango"},
    type = "regular",
    scripts = {"Latn"},
    family = "crp",
}

m["sh"] = {
    names = {"Serbo-Croatian", "BCS", "Croato-Serbian", "Serbocroatian", "Bosnian", "Croatian", "Montenegrin", "Serbian"},
    type = "regular",
    scripts = {"Latn", "Cyrl"},
    family = "zls",
    entry_name = {
        from = {"[ȀÀȂÁĀ]", "[ȁàȃáā]", "[ȄÈȆÉĒ]", "[ȅèȇéē]", "[ȈÌȊÍĪ]", "[ȉìȋíī]", "[ȌÒȎÓŌ]", "[ȍòȏóō]", "[ȐȒŔ]", "[ȑȓŕ]", "[ȔÙȖÚŪ]", "[ȕùȗúū]", "Ѐ", "ѐ", "[ӢЍ]", "[ӣѝ]", "[Ӯ]", "[ӯ]", GRAVE, ACUTE, DGRAVE, INVBREVE, MACRON},
        to   = {"A"	  , "a"	  , "E"	  , "e"	  , "I"	  , "i"	  , "O"	  , "o"	  , "R"	, "r"	, "U"	  , "u"	  , "Е", "е", "И"   , "и", "У", "у"   }},
    wikimedia_codes = {"sh", "bs", "hr", "sr"},
}

m["si"] = {
    names = {"Sinhalese", "Singhalese", "Sinhala"},
    type = "regular",
    scripts = {"Sinh"},
    family = "inc",
    translit_module = "si-translit",
}

m["sk"] = {
    names = {"Slovak"},
    type = "regular",
    scripts = {"Latn"},
    family = "zlw",
    sort_key = {
        from = {"[áä]", "é", "í", "[óô]", "ú", "ý", "ŕ", "ĺ"},
        to   = {"a"   , "e", "i", "o"   , "u", "y", "r", "l"}} ,
}

m["sl"] = {
    names = {"Slovene", "Slovenian"},
    type = "regular",
    scripts = {"Latn"},
    family = "zls",
    entry_name = {
        from = {"[ÁÀÂȂȀ]", "[áàâȃȁ]", "[ÉÈÊȆȄỆẸ]", "[éèêȇȅệẹə]", "[ÍÌÎȊȈ]", "[íìîȋȉ]", "[ÓÒÔȎȌỘỌ]", "[óòôȏȍộọ]", "[ŔȒȐ]", "[ŕȓȑ]", "[ÚÙÛȖȔ]", "[úùûȗȕ]", "ł", GRAVE, ACUTE, DGRAVE, INVBREVE, CIRC, DOTBELOW},
        to   = {"A"	  , "a"	  , "E"		, "e"		 , "I"	  , "i"	  , "O"		, "o"		, "R"	, "r"	, "U"	  , "u"	  , "l"}} ,
}

m["sm"] = {
    names = {"Samoan"},
    type = "regular",
    scripts = {"Latn"},
    family = "poz-pol",
}

m["sn"] = {
    names = {"Shona"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["so"] = {
    names = {"Somali"},
    type = "regular",
    scripts = {"Latn", "Arab", "Osma"},
    family = "cus",
    entry_name = {
        from = {"[ÁÀÂ]", "[áàâ]", "[ÉÈÊ]", "[éèê]", "[ÍÌÎ]", "[íìî]", "[ÓÒÔ]", "[óòô]", "[ÚÙÛ]", "[úùû]", "[ÝỲ]", "[ýỳ]"},
        to   = {"A"	  , "a"	  , "E"	, "e" , "I"	  , "i"	  , "O"	, "o"	, "U"  , "u", "Y", "y"}} ,
}

m["sq"] = {
    names = {"Albanian"},
    type = "regular",
    scripts = {"Latn"},
    family = "sqj",
    sort_key = {
        from = { '[âãä]', '[ÂÃÄ]', '[êẽë]', '[ÊẼË]', 'ĩ', 'Ĩ', 'õ', 'Õ', 'ũ', 'Ũ', 'ỹ', 'Ỹ', 'ç', 'Ç' },
        to   = {     'a',     'A',     'e',     'E', 'i', 'I', 'o', 'O', 'u', 'U', 'y', 'Y', 'c', 'C' } } ,
}

m["ss"] = {
    names = {"Swazi", "Swati"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["st"] = {
    names = {"Sotho", "Sesotho", "Southern Sesotho", "Southern Sotho"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["su"] = {
    names = {"Sundanese"},
    type = "regular",
    scripts = {"Latn", "Sund"},
    family = "poz-msa",
}

m["sv"] = {
    names = {"Swedish"},
    type = "regular",
    scripts = {"Latn"},
    family = "gmq",
    ancestors = {"gmq-osw"},
}

m["sw"] = {
    names = {"Swahili"},
    type = "regular",
    scripts = {"Latn", "Arab"},
    family = "bnt",
    sort_key = {
        from = {"ng'", "^-"},
        to   = {"ngz"}} ,
}

m["ta"] = {
    names = {"Tamil"},
    type = "regular",
    scripts = {"Taml"},
    family = "dra",
    translit_module = "ta-translit",
}

m["te"] = {
    names = {"Telugu"},
    type = "regular",
    scripts = {"Telu"},
    family = "dra",
    translit_module = "te-translit",
}

m["tg"] = {
    names = {"Tajik", "Tadjik", "Tadzhik", "Tajiki", "Tajik Persian"},
    type = "regular",
    scripts = {"Cyrl", "fa-Arab", "Latn"},
    family = "ira",
    translit_module = "tg-translit",
    sort_key = {
        from = {"Ё", "ё"},
        to   = {"Е" , "е"}} ,
}

m["th"] = {
    names = {"Thai"},
    type = "regular",
    scripts = {"Thai"},
    family = "tai-swe",
}

m["ti"] = {
    names = {"Tigrinya"},
    type = "regular",
    scripts = {"Ethi"},
    family = "sem-eth",
    translit_module = "Ethi-translit",
}

m["tk"] = {
    names = {"Turkmen"},
    type = "regular",
    scripts = {"Latn", "Cyrl"},
    family = "trk",
}

m["tl"] = {
    names = {"Tagalog"},
    type = "regular",
    scripts = {"Latn", "Tglg"},
    family = "phi",
}

m["tn"] = {
    names = {"Tswana", "Setswana"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["to"] = {
    names = {"Tongan"},
    type = "regular",
    scripts = {"Latn"},
    family = "poz-pol",
}

m["tr"] = {
    names = {"Turkish"},
    type = "regular",
    scripts = {"Latn"},
    family = "trk",
}

m["ts"] = {
    names = {"Tsonga"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["tt"] = {
    names = {"Tatar"},
    type = "regular",
    scripts = {"Cyrl", "Latn", "Arab", "tt-Arab"},
    family = "trk",
    translit_module = "tt-translit",
}

m["tw"] = {
    names = {"Twi"},
    type = "regular",
    scripts = {"Latn"},
    family = "alv-kwa",
}

m["ty"] = {
    names = {"Tahitian"},
    type = "regular",
    scripts = {"Latn"},
    family = "poz-pol",
}

m["ug"] = {
    names = {"Uyghur", "Uigur", "Uighur", "Uygur"},
    type = "regular",
    scripts = {"ug-Arab", "Latn", "Cyrl"},
    family = "trk",
    translit_module = "ug-translit",
}

m["uk"] = {
    names = {"Ukrainian"},
    type = "regular",
    scripts = {"Cyrl"},
    family = "zle",
    ancestors = {"orv"},
    translit_module = "uk-translit",
    entry_name = {
        from = {GRAVE, ACUTE},
        to   = {}} }
m["ur"] = {
    names = {"Urdu"},
    type = "regular",
    scripts = {"ur-Arab"},
    family = "inc",
    entry_name = {
        from = {u(0x064B), u(0x064C), u(0x064D), u(0x064E), u(0x064F), u(0x0650), u(0x0651), u(0x0652)},
        to   = {}} ,
}

m["uz"] = {
    names = {"Uzbek"},
    type = "regular",
    scripts = {"Latn", "Cyrl", "fa-Arab"},
    family = "trk",
}

m["ve"] = {
    names = {"Venda"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["vi"] = {
    names = {"Vietnamese", "Annamese", "Annamite"},
    type = "regular",
    scripts = {"Latn", "Hani"},
    family = "mkh-vie",
}

m["vo"] = {
    names = {"Volapük"},
    type = "regular",
    scripts = {"Latn"},
    family = "art",
}

m["wa"] = {
    names = {"Walloon"},
    type = "regular",
    scripts = {"Latn"},
    family = "roa",
    sort_key = {
        from = {"[áàâäå]", "[éèêë]", "[íìîï]", "[óòôö]", "[úùûü]", "[ýỳŷÿ]", "ç", "'"},
        to   = {"a"	  , "e"	 , "i"	 , "o"	 , "u"	 , "y"	 , "c"}} ,
}

m["wo"] = {
    names = {"Wolof"},
    type = "regular",
    scripts = {"Latn", "Arab"},
    family = "alv-sng",
}

m["xh"] = {
    names = {"Xhosa"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

m["yi"] = {
    names = {"Yiddish"},
    type = "regular",
    scripts = {"Hebr"},
    family = "gmw",
    ancestors = {"gmh"},
    translit_module = "yi-translit",
}

m["yo"] = {
    names = {"Yoruba"},
    type = "regular",
    scripts = {"Latn"},
    family = "alv-von",
}

m["za"] = {
    names = {"Zhuang"},
    type = "regular",
    scripts = {"Latn", "Hani", "Cyrl"},
    family = "tai",
}

m["zh"] = {
    names = {"Chinese"},
    type = "regular",
    scripts = {"Hani"},
    family = "sit",
}

m["zu"] = {
    names = {"Zulu", "isiZulu"},
    type = "regular",
    scripts = {"Latn"},
    family = "bnt",
}

return m
