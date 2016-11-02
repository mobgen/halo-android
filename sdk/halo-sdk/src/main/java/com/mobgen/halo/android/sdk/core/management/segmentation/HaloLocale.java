package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Locale;

/**
 * Provides all the locale constants for the given device.
 */
@Keep
public final class HaloLocale {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({AFAR_DJIBOUTI,
            AFAR_ERITREA,
            AFAR_ETHIOPIA,
            ABKHAZIAN,
            AVESTAN,
            AFRIKAANS,
            AKAN,
            AMHARIC,
            ARAGONESE,
            ARABIC_UNITED_ARAB_EMIRATES,
            ARABIC_BAHRAIN,
            ARABIC_ALGERIA,
            ARABIC_EGYPT,
            ARABIC_ISRAEL,
            ARABIC_INDIA,
            ARABIC_IRAQ,
            ARABIC_JORDAN,
            ARABIC_KUWAIT,
            ARABIC_LEBANON,
            ARABIC_LIBYAN_ARAB_JAMAHIRIYA,
            ARABIC_MOROCCO,
            ARABIC_MAURITANIA,
            ARABIC_OMAN,
            ARABIC_PALESTINIAN_TERRITORY,
            ARABIC_QATAR,
            ARABIC_SAUDI_ARABIA,
            ARABIC_SUDAN,
            ARABIC_SOMALIA,
            ARABIC_SYRIAN_ARAB_REPUBLIC,
            ARABIC_CHAD,
            ARABIC_TUNISIA,
            ARABIC_YEMEN,
            ASSAMESE,
            AVARIC,
            AYMARA,
            AZERBAIJANI,
            BASHKIR,
            BELARUSIAN,
            BULGARIAN,
            BIHARI,
            BISLAMA,
            BAMBARA,
            BENGALI_BANGLADESH,
            BENGALI_INDIA,
            BENGALI_SINGAPORE,
            TIBETAN,
            BRETON,
            BOSNIAN,
            BLIN,
            CATALAN,
            CHECHEN,
            CHAMORRO_GUAM,
            CHAMORRO_NORTHERN_MARIANA_ISLANDS,
            CORSICAN,
            CREE,
            CZECH,
            CHURCH_SLAVIC,
            CHUVASH,
            WELSH_ARGENTINA,
            WELSH_UNITED_KINGDOM,
            DANISH_GERMANY,
            DANISH_DENMARK,
            DANISH_FAROE_ISLANDS,
            DANISH_GREENLAND,
            GERMAN_AUSTRIA,
            GERMAN_BELGIUM,
            GERMAN_SWITZERLAND,
            GERMAN_GERMANY,
            GERMAN_DENMARK,
            GERMAN_FRANCE,
            GERMAN_HUNGARY,
            GERMAN_ITALY,
            GERMAN_LIECHTENSTEIN,
            GERMAN_LUXEMBOURG,
            GERMAN_POLAND,
            DINKA,
            LOWER_SORBIAN,
            DIVEHI,
            DZONGKHA,
            EWE,
            GREEK_CYPRUS,
            GREEK_GREECE,
            ENGLISH_ANTIGUA_AND_BARBUDA,
            ENGLISH_ANGUILLA,
            ENGLISH_AMERICAN_SAMOA,
            ENGLISH_AUSTRALIA,
            ENGLISH_BARBADOS,
            ENGLISH_BELGIUM,
            ENGLISH_BERMUDA,
            ENGLISH_BRUNEI_DARUSSALAM,
            ENGLISH_BAHAMAS,
            ENGLISH_BOTSWANA,
            ENGLISH_BELIZE,
            ENGLISH_CANADA,
            ENGLISH_COOK_ISLANDS,
            ENGLISH_CAMEROON,
            ENGLISH_DOMINICA,
            ENGLISH_ERITREA,
            ENGLISH_ETHIOPIA,
            ENGLISH_FIJI,
            ENGLISH_FALKLAND_ISLANDS_MALVINAS,
            ENGLISH_MICRONESIA,
            ENGLISH_UNITED_KINGDOM,
            ENGLISH_GRENADA,
            ENGLISH_GHANA,
            ENGLISH_GIBRALTAR,
            ENGLISH_GAMBIA,
            ENGLISH_GUAM,
            ENGLISH_GUYANA,
            ENGLISH_HONG_KONG,
            ENGLISH_IRELAND,
            ENGLISH_ISRAEL,
            ENGLISH_INDIA,
            ENGLISH_BRITISH_INDIAN_OCEAN_TERRITORY,
            ENGLISH_JAMAICA,
            ENGLISH_KENYA,
            ENGLISH_KIRIBATI,
            ENGLISH_SAINT_KITTS_AND_NEVIS,
            ENGLISH_CAYMAN_ISLANDS,
            ENGLISH_SAINT_LUCIA,
            ENGLISH_LIBERIA,
            ENGLISH_LESOTHO,
            ENGLISH_MARSHALL_ISLANDS,
            ENGLISH_NORTHERN_MARIANA_ISLANDS,
            ENGLISH_MONTSERRAT,
            ENGLISH_MALTA,
            ENGLISH_MAURITIUS,
            ENGLISH_MALAWI,
            ENGLISH_NAMIBIA,
            ENGLISH_NORFOLK_ISLAND,
            ENGLISH_NIGERIA,
            ENGLISH_NAURU,
            ENGLISH_NIUE,
            ENGLISH_NEW_ZEALAND,
            ENGLISH_PAPUA_NEW_GUINEA,
            ENGLISH_PHILIPPINES,
            ENGLISH_PAKISTAN,
            ENGLISH_PITCAIRN,
            ENGLISH_PUERTO_RICO,
            ENGLISH_PALAU,
            ENGLISH_RWANDA,
            ENGLISH_SOLOMON_ISLANDS,
            ENGLISH_SEYCHELLES,
            ENGLISH_SINGAPORE,
            ENGLISH_SAINT_HELENA,
            ENGLISH_SIERRA_LEONE,
            ENGLISH_SOMALIA,
            ENGLISH_SWAZILAND,
            ENGLISH_TURKS_AND_CAICOS_ISLANDS,
            ENGLISH_TOKELAU,
            ENGLISH_TONGA,
            ENGLISH_TRINIDAD_AND_TOBAGO,
            ENGLISH_UGANDA,
            ENGLISH_UNITED_STATES_MINOR_OUTLYING_ISLANDS,
            ENGLISH_UNITED_STATES,
            ENGLISH_SAINT_VINCENT_AND_THE_GRENADINES,
            ENGLISH_VIRGIN_ISLANDS_BRITISH,
            ENGLISH_VIRGIN_ISLANDS_US,
            ENGLISH_VANUATU,
            ENGLISH_SAMOA,
            ENGLISH_SOUTH_AFRICA,
            ENGLISH_ZAMBIA,
            ENGLISH_ZIMBABWE,
            ESPERANTO,
            SPANISH_ARGENTINA,
            SPANISH_BOLIVIA,
            SPANISH_CHILE,
            SPANISH_COLOMBIA,
            SPANISH_COSTA_RICA,
            SPANISH_CUBA,
            SPANISH_DOMINICAN_REPUBLIC,
            SPANISH_ECUADOR,
            SPANISH_SPAIN,
            SPANISH_EQUATORIAL_GUINEA,
            SPANISH_GUATEMALA,
            SPANISH_HONDURAS,
            SPANISH_MEXICO,
            SPANISH_NICARAGUA,
            SPANISH_PANAMA,
            SPANISH_PERU,
            SPANISH_PUERTO_RICO,
            SPANISH_PARAGUAY,
            SPANISH_EL_SALVADOR,
            SPANISH_UNITED_STATES,
            SPANISH_URUGUAY,
            SPANISH_VENEZUELA,
            ESTONIAN,
            BASQUE,
            PERSIAN_AFGHANISTAN,
            PERSIAN_IRAN,
            FULAH_NIGER,
            FULAH_NIGERIA,
            FULAH_SENEGAL,
            FINNISH_FINLAND,
            FINNISH_SWEDEN,
            FIJIAN,
            FAROESE,
            FRENCH_HOLY_SEE_VATICAN_CITY_STATE,
            FRENCH_ANDORRA,
            FRENCH_BELGIUM,
            FRENCH_BURKINA_FASO,
            FRENCH_BURUNDI,
            FRENCH_BENIN,
            FRENCH_CANADA,
            FRENCH_THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO,
            FRENCH_CENTRAL_AFRICAN_REPUBLIC,
            FRENCH_CONGO,
            FRENCH_SWITZERLAND,
            FRENCH_COTE_DIVOIRE,
            FRENCH_CAMEROON,
            FRENCH_DJIBOUTI,
            FRENCH_FRANCE,
            FRENCH_GABON,
            FRENCH_UNITED_KINGDOM,
            FRENCH_GUIANA,
            FRENCH_GUINEA,
            FRENCH_GUADELOUPE,
            FRENCH_HAITI,
            FRENCH_ITALY,
            FRENCH_COMOROS,
            FRENCH_LEBANON,
            FRENCH_LUXEMBOURG,
            FRENCH_MONACO,
            FRENCH_MADAGASCAR,
            FRENCH_MALI,
            FRENCH_MARTINIQUE,
            FRENCH_NEW_CALEDONIA,
            FRENCH_NIGER,
            FRENCH_FRENCH_POLYNESIA,
            FRENCH_SAINT_PIERRE_AND_MIQUELON,
            FRENCH_REUNION,
            FRENCH_RWANDA,
            FRENCH_SEYCHELLES,
            FRENCH_CHAD,
            FRENCH_TOGO,
            FRENCH_VANUATU,
            FRENCH_WALLIS_AND_FUTUNA,
            FRENCH_MAYOTTE,
            FRISIAN_GERMAN,
            FRISIAN_NETHERLANDS,
            IRISH_UNITED_KINGDOM,
            IRISH_IRELAND,
            GAELIC,
            GEEZ_ERITREA,
            GEEZ_ETHIOPIA,
            GILBERTESE,
            GALICIAN,
            GUARANI,
            GUJARATI,
            MANX,
            HAUSA,
            HAWAIIAN,
            HEBREW,
            HINDI,
            HIRI_MOTU,
            CROATIAN_BOSNIA_AND_HERZEGOVINA,
            CROATIAN_CROATIA,
            UPPER_SORBIAN,
            HAITIAN,
            HUNGARIAN,
            HUNGARIAN_HUNGARY,
            HUNGARIAN_SLOVENIA,
            ARMENIAN,
            HERERO,
            INTERLINGUA,
            INDONESIAN,
            INTERLINGUE,
            IGBO,
            SICHUAN_YI,
            INUPIAQ,
            IDO,
            ICELANDIC,
            ITALIAN,
            ITALIAN_SWITZERLAND,
            ITALIAN_CROATIA,
            ITALIAN_ITALY,
            ITALIAN_SLOVENIA,
            ITALIAN_SAN_MARINO,
            INUKTITUT,
            JAPANESE,
            JAVANESE,
            GEORGIAN,
            KONGO,
            KIKUYU,
            KUANYAMA,
            KAZAKH,
            KALAALLISUT,
            KHMER,
            KANNADA,
            KOREAN_DEMOCRATIC_PEOPLES_REPUBLIC_OF_KOREA,
            KOREAN_REPUBLIC_OF_KOREA,
            KONKANI,
            KANURI,
            KASHMIRI,
            KURDISH,
            KOMI,
            CORNISH,
            KIRGHIZ,
            LATIN,
            LUXEMBOURGISH,
            GANDA,
            LIMBURGAN,
            LINGALA_THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO,
            LINGALA_CONGO,
            LAO,
            LITHUANIAN,
            LUBA_KATANGA,
            LATVIAN,
            MALAGASY,
            MARSHALLESE,
            MAORI,
            MACEDONIAN,
            MALAYALAM,
            MONGOLIAN,
            MOLDAVIAN,
            MARATHI,
            MALAY_BRUNEI_DARUSSALAM,
            MALAY_COCOS_KEELING_ISLANDS,
            MALAY_MALAYSIA,
            MALAY_SINGAPORE,
            MALTESE,
            BURMESE,
            NAURU,
            BOKMAL,
            N_NDEBELE,
            LOW_GERMAN,
            NEPALI,
            NDONGA,
            DUTCH_NETHERLANDS_ANTILLES,
            DUTCH_ARUBA,
            DUTCH_BELGIUM,
            DUTCH_NETHERLANDS,
            DUTCH_SURINAME,
            NYNORSK,
            NORWEGIAN,
            S_NDEBELE,
            NAVAJO,
            CHICHEWA,
            OCCITAN,
            OJIBWA,
            OROMO_ETHIOPIA,
            OROMO_KENYA,
            ORIYA,
            OSSETIAN,
            PANJABI,
            PALI,
            POLISH,
            PUSHTO,
            PORTUGUESE_ANGOLA,
            PORTUGUESE_BRAZIL,
            PORTUGUESE_CAPE_VERDE,
            PORTUGUESE_GUINEA_BISSAU,
            PORTUGUESE_MOZAMBIQUE,
            PORTUGUESE_PORTUGAL,
            PORTUGUESE_SAO_TOME_AND_PRINCIPE,
            PORTUGUESE_TIMOR_LESTE,
            QUECHUA,
            RAETO_ROMANCE,
            RUNDI,
            ROMANIAN,
            RUSSIAN_RUSSIAN_FEDERATION,
            RUSSIAN_UKRAINE,
            KINYARWANDA,
            SANSKRIT,
            SARDINIAN,
            SINDHI_INDIA,
            SINDHI_PAKISTAN,
            NORTHERN_SAMI,
            SANGO,
            SINHALA,
            SIDAMO,
            SLOVAK,
            SLOVAK_HUNGARY,
            SAMOAN,
            S_SAMI,
            N_SAMI,
            INARI_SAMI,
            SHONA,
            SOMALI_DJIBOUTI,
            SOMALI_ETHIOPIA,
            SOMALI_KENYA,
            SOMALI_SOMALIA,
            ALBANIAN,
            SERBIAN,
            SERBIAN_BOSNIA_AND_HERZEGOVINA,
            SERBIAN_HUNGARY,
            SWATI_SWAZILAND,
            SWATI_SOUTH_AFRICA,
            SOUTHERN_SOTHO,
            SUNDANESE,
            SWEDISH_ALAND_ISLANDS,
            SWEDISH_FINLAND,
            SWEDISH_SWEDEN,
            SWAHILI_KENYA,
            SWAHILI_UNITED_REPUBLIC_OF_TANZANIA,
            SYRIAC,
            TAMIL_INDIA,
            TAMIL_SINGAPORE,
            TELUGU,
            TAJIK,
            THAI,
            TIGRINYA_ERITREA,
            TIGRINYA_ETHIOPIA,
            TIGRE,
            TURKMEN,
            TAGALOG,
            TSWANA_TSWANA,
            TSWANA_SOUTH_AFRICA,
            TONGAN,
            TURKISH,
            TURKISH_BULGARIA,
            TURKISH_CYPRUS,
            TURKISH_TURKEY,
            TSONGA,
            TATAR,
            TUVALU,
            TWI,
            TAHITIAN,
            UIGHUR,
            UKRAINIAN,
            URDU_INDIA,
            URDU_PAKISTAN,
            UZBEK_AFGHANISTAN,
            UZBEK_UZBEKISTAN,
            VENDA,
            VIETNAMESE,
            VOLAPUK,
            WALLOON,
            WALAMO,
            SORBIAN,
            WOLOF,
            XHOSA,
            YIDDISH,
            YORUBA,
            ZHUANG,
            CHINESE_CHINA,
            CHINESE_HONG_KONG,
            CHINESE_TRADITIONAL_SINGAPORE,
            CHINESE_TRADITIONAL_HONG_KONG,
            CHINESE_MACAO,
            CHINESE_SINGAPORE,
            CHINESE_TAIWAN,
            ZULU})
    /**
     * Locale definition annotation to ensure the value passed is valid.
     */
    @Keep
    public @interface LocaleDefinition {
    }

    /**
     * aa-DJ
     */
    public static final String AFAR_DJIBOUTI = "aa-DJ";
    /**
     * aa-ER
     */
    public static final String AFAR_ERITREA = "aa-ER";
    /**
     * aa-ET
     */
    public static final String AFAR_ETHIOPIA = "aa-ET";
    /**
     * ab
     */
    public static final String ABKHAZIAN = "ab";
    /**
     * ae
     */
    public static final String AVESTAN = "ae";
    /**
     * af
     */
    public static final String AFRIKAANS = "af";
    /**
     * ak
     */
    public static final String AKAN = "ak";
    /**
     * am
     */
    public static final String AMHARIC = "am";
    /**
     * an
     */
    public static final String ARAGONESE = "an";
    /**
     * ar-AE
     */
    public static final String ARABIC_UNITED_ARAB_EMIRATES = "ar-AE";
    /**
     * ar-BH
     */
    public static final String ARABIC_BAHRAIN = "ar-BH";
    /**
     * ar-DZ
     */
    public static final String ARABIC_ALGERIA = "ar-DZ";
    /**
     * ar-EG
     */
    public static final String ARABIC_EGYPT = "ar-EG";
    /**
     * ar-IL
     */
    public static final String ARABIC_ISRAEL = "ar-IL";
    /**
     * ar-IN
     */
    public static final String ARABIC_INDIA = "ar-IN";
    /**
     * ar-IQ
     */
    public static final String ARABIC_IRAQ = "ar-IQ";
    /**
     * ar-JO
     */
    public static final String ARABIC_JORDAN = "ar-JO";
    /**
     * ar-KW
     */
    public static final String ARABIC_KUWAIT = "ar-KW";
    /**
     * ar-LB
     */
    public static final String ARABIC_LEBANON = "ar-LB";
    /**
     * ar-LY
     */
    public static final String ARABIC_LIBYAN_ARAB_JAMAHIRIYA = "ar-LY";
    /**
     * ar-MA
     */
    public static final String ARABIC_MOROCCO = "ar-MA";
    /**
     * ar-MR
     */
    public static final String ARABIC_MAURITANIA = "ar-MR";
    /**
     * ar-OM
     */
    public static final String ARABIC_OMAN = "ar-OM";
    /**
     * ar-PS
     */
    public static final String ARABIC_PALESTINIAN_TERRITORY = "ar-PS";
    /**
     * ar-QA
     */
    public static final String ARABIC_QATAR = "ar-QA";
    /**
     * ar-SA
     */
    public static final String ARABIC_SAUDI_ARABIA = "ar-SA";
    /**
     * ar-SD
     */
    public static final String ARABIC_SUDAN = "ar-SD";
    /**
     * ar-SO
     */
    public static final String ARABIC_SOMALIA = "ar-SO";
    /**
     * ar-SY
     */
    public static final String ARABIC_SYRIAN_ARAB_REPUBLIC = "ar-SY";
    /**
     * ar-TD
     */
    public static final String ARABIC_CHAD = "ar-TD";
    /**
     * ar-TN
     */
    public static final String ARABIC_TUNISIA = "ar-TN";
    /**
     * ar-YE
     */
    public static final String ARABIC_YEMEN = "ar-YE";
    /**
     * as
     */
    public static final String ASSAMESE = "as";
    /**
     * av
     */
    public static final String AVARIC = "av";
    /**
     * ay
     */
    public static final String AYMARA = "ay";
    /**
     * az
     */
    public static final String AZERBAIJANI = "az";
    /**
     * ba
     */
    public static final String BASHKIR = "ba";
    /**
     * be
     */
    public static final String BELARUSIAN = "be";
    /**
     * bg
     */
    public static final String BULGARIAN = "bg";
    /**
     * bh
     */
    public static final String BIHARI = "bh";
    /**
     * bi
     */
    public static final String BISLAMA = "bi";
    /**
     * bm
     */
    public static final String BAMBARA = "bm";
    /**
     * bn-BD
     */
    public static final String BENGALI_BANGLADESH = "bn-BD";
    /**
     * bn-IN
     */
    public static final String BENGALI_INDIA = "bn-IN";
    /**
     * bn-SG
     */
    public static final String BENGALI_SINGAPORE = "bn-SG";
    /**
     * bo
     */
    public static final String TIBETAN = "bo";
    /**
     * br
     */
    public static final String BRETON = "br";
    /**
     * bs
     */
    public static final String BOSNIAN = "bs";
    /**
     * byn
     */
    public static final String BLIN = "byn";
    /**
     * ca
     */
    public static final String CATALAN = "ca";
    /**
     * ce
     */
    public static final String CHECHEN = "ce";
    /**
     * ch-GU
     */
    public static final String CHAMORRO_GUAM = "ch-GU";
    /**
     * ch-MP
     */
    public static final String CHAMORRO_NORTHERN_MARIANA_ISLANDS = "ch-MP";
    /**
     * co
     */
    public static final String CORSICAN = "co";
    /**
     * cr
     */
    public static final String CREE = "cr";
    /**
     * cs
     */
    public static final String CZECH = "cs";
    /**
     * cu
     */
    public static final String CHURCH_SLAVIC = "cu";
    /**
     * cv
     */
    public static final String CHUVASH = "cv";
    /**
     * cy-AR
     */
    public static final String WELSH_ARGENTINA = "cy-AR";
    /**
     * cy-GB
     */
    public static final String WELSH_UNITED_KINGDOM = "cy-GB";
    /**
     * da-DE
     */
    public static final String DANISH_GERMANY = "da-DE";
    /**
     * da-DK
     */
    public static final String DANISH_DENMARK = "da-DK";
    /**
     * da-FO
     */
    public static final String DANISH_FAROE_ISLANDS = "da-FO";
    /**
     * da-GL
     */
    public static final String DANISH_GREENLAND = "da-GL";
    /**
     * de-AT
     */
    public static final String GERMAN_AUSTRIA = "de-AT";
    /**
     * de-BE
     */
    public static final String GERMAN_BELGIUM = "de-BE";
    /**
     * de-CH
     */
    public static final String GERMAN_SWITZERLAND = "de-CH";
    /**
     * de-DE
     */
    public static final String GERMAN_GERMANY = "de-DE";
    /**
     * de-DK
     */
    public static final String GERMAN_DENMARK = "de-DK";
    /**
     * de-FR
     */
    public static final String GERMAN_FRANCE = "de-FR";
    /**
     * de-HU
     */
    public static final String GERMAN_HUNGARY = "de-HU";
    /**
     * de-IT
     */
    public static final String GERMAN_ITALY = "de-IT";
    /**
     * de-LI
     */
    public static final String GERMAN_LIECHTENSTEIN = "de-LI";
    /**
     * de-LU
     */
    public static final String GERMAN_LUXEMBOURG = "de-LU";
    /**
     * de-PL
     */
    public static final String GERMAN_POLAND = "de-PL";
    /**
     * din
     */
    public static final String DINKA = "din";
    /**
     * dsb
     */
    public static final String LOWER_SORBIAN = "dsb";
    /**
     * dv
     */
    public static final String DIVEHI = "dv";
    /**
     * dz
     */
    public static final String DZONGKHA = "dz";
    /**
     * ee
     */
    public static final String EWE = "ee";
    /**
     * el-CY
     */
    public static final String GREEK_CYPRUS = "el-CY";
    /**
     * el-GR
     */
    public static final String GREEK_GREECE = "el-GR";
    /**
     * en-AG
     */
    public static final String ENGLISH_ANTIGUA_AND_BARBUDA = "en-AG";
    /**
     * en-AI
     */
    public static final String ENGLISH_ANGUILLA = "en-AI";
    /**
     * en-AS
     */
    public static final String ENGLISH_AMERICAN_SAMOA = "en-AS";
    /**
     * en-AU
     */
    public static final String ENGLISH_AUSTRALIA = "en-AU";
    /**
     * en-BB
     */
    public static final String ENGLISH_BARBADOS = "en-BB";
    /**
     * en-BE
     */
    public static final String ENGLISH_BELGIUM = "en-BE";
    /**
     * en-BM
     */
    public static final String ENGLISH_BERMUDA = "en-BM";
    /**
     * en-BN
     */
    public static final String ENGLISH_BRUNEI_DARUSSALAM = "en-BN";
    /**
     * en-BS
     */
    public static final String ENGLISH_BAHAMAS = "en-BS";
    /**
     * en-BW
     */
    public static final String ENGLISH_BOTSWANA = "en-BW";
    /**
     * en-BZ
     */
    public static final String ENGLISH_BELIZE = "en-BZ";
    /**
     * en-CA
     */
    public static final String ENGLISH_CANADA = "en-CA";
    /**
     * en-CK
     */
    public static final String ENGLISH_COOK_ISLANDS = "en-CK";
    /**
     * en-CM
     */
    public static final String ENGLISH_CAMEROON = "en-CM";
    /**
     * en-DM
     */
    public static final String ENGLISH_DOMINICA = "en-DM";
    /**
     * en-ER
     */
    public static final String ENGLISH_ERITREA = "en-ER";
    /**
     * en-ET
     */
    public static final String ENGLISH_ETHIOPIA = "en-ET";
    /**
     * en-FJ
     */
    public static final String ENGLISH_FIJI = "en-FJ";
    /**
     * en-FK
     */
    public static final String ENGLISH_FALKLAND_ISLANDS_MALVINAS = "en-FK";
    /**
     * en-FM
     */
    public static final String ENGLISH_MICRONESIA = "en-FM";
    /**
     * en-GB
     */
    public static final String ENGLISH_UNITED_KINGDOM = "en-GB";
    /**
     * en-GD
     */
    public static final String ENGLISH_GRENADA = "en-GD";
    /**
     * en-GH
     */
    public static final String ENGLISH_GHANA = "en-GH";
    /**
     * en-GI
     */
    public static final String ENGLISH_GIBRALTAR = "en-GI";
    /**
     * en-GM
     */
    public static final String ENGLISH_GAMBIA = "en-GM";
    /**
     * en-GU
     */
    public static final String ENGLISH_GUAM = "en-GU";
    /**
     * en-GY
     */
    public static final String ENGLISH_GUYANA = "en-GY";
    /**
     * en-HK
     */
    public static final String ENGLISH_HONG_KONG = "en-HK";
    /**
     * en-IE
     */
    public static final String ENGLISH_IRELAND = "en-IE";
    /**
     * en-IL
     */
    public static final String ENGLISH_ISRAEL = "en-IL";
    /**
     * en-IN
     */
    public static final String ENGLISH_INDIA = "en-IN";
    /**
     * en-IO
     */
    public static final String ENGLISH_BRITISH_INDIAN_OCEAN_TERRITORY = "en-IO";
    /**
     * en-JM
     */
    public static final String ENGLISH_JAMAICA = "en-JM";
    /**
     * en-KE
     */
    public static final String ENGLISH_KENYA = "en-KE";
    /**
     * en-KI
     */
    public static final String ENGLISH_KIRIBATI = "en-KI";
    /**
     * en-KN
     */
    public static final String ENGLISH_SAINT_KITTS_AND_NEVIS = "en-KN";
    /**
     * en-KY
     */
    public static final String ENGLISH_CAYMAN_ISLANDS = "en-KY";
    /**
     * en-LC
     */
    public static final String ENGLISH_SAINT_LUCIA = "en-LC";
    /**
     * en-LR
     */
    public static final String ENGLISH_LIBERIA = "en-LR";
    /**
     * en-LS
     */
    public static final String ENGLISH_LESOTHO = "en-LS";
    /**
     * en-MH
     */
    public static final String ENGLISH_MARSHALL_ISLANDS = "en-MH";
    /**
     * en-MP
     */
    public static final String ENGLISH_NORTHERN_MARIANA_ISLANDS = "en-MP";
    /**
     * en-MS
     */
    public static final String ENGLISH_MONTSERRAT = "en-MS";
    /**
     * en-MT
     */
    public static final String ENGLISH_MALTA = "en-MT";
    /**
     * en-MU
     */
    public static final String ENGLISH_MAURITIUS = "en-MU";
    /**
     * en-MW
     */
    public static final String ENGLISH_MALAWI = "en-MW";
    /**
     * en-NA
     */
    public static final String ENGLISH_NAMIBIA = "en-NA";
    /**
     * en-NF
     */
    public static final String ENGLISH_NORFOLK_ISLAND = "en-NF";
    /**
     * en-NG
     */
    public static final String ENGLISH_NIGERIA = "en-NG";
    /**
     * en-NR
     */
    public static final String ENGLISH_NAURU = "en-NR";
    /**
     * en-NU
     */
    public static final String ENGLISH_NIUE = "en-NU";
    /**
     * en-NZ
     */
    public static final String ENGLISH_NEW_ZEALAND = "en-NZ";
    /**
     * en-PG
     */
    public static final String ENGLISH_PAPUA_NEW_GUINEA = "en-PG";
    /**
     * en-PH
     */
    public static final String ENGLISH_PHILIPPINES = "en-PH";
    /**
     * en-PK
     */
    public static final String ENGLISH_PAKISTAN = "en-PK";
    /**
     * en-PN
     */
    public static final String ENGLISH_PITCAIRN = "en-PN";
    /**
     * en-PR
     */
    public static final String ENGLISH_PUERTO_RICO = "en-PR";
    /**
     * en-PW
     */
    public static final String ENGLISH_PALAU = "en-PW";
    /**
     * en-RW
     */
    public static final String ENGLISH_RWANDA = "en-RW";
    /**
     * en-SB
     */
    public static final String ENGLISH_SOLOMON_ISLANDS = "en-SB";
    /**
     * en-SC
     */
    public static final String ENGLISH_SEYCHELLES = "en-SC";
    /**
     * en-SG
     */
    public static final String ENGLISH_SINGAPORE = "en-SG";
    /**
     * en-SH
     */
    public static final String ENGLISH_SAINT_HELENA = "en-SH";
    /**
     * en-SL
     */
    public static final String ENGLISH_SIERRA_LEONE = "en-SL";
    /**
     * en-SO
     */
    public static final String ENGLISH_SOMALIA = "en-SO";
    /**
     * en-SZ
     */
    public static final String ENGLISH_SWAZILAND = "en-SZ";
    /**
     * en-TC
     */
    public static final String ENGLISH_TURKS_AND_CAICOS_ISLANDS = "en-TC";
    /**
     * en-TK
     */
    public static final String ENGLISH_TOKELAU = "en-TK";
    /**
     * en-TO
     */
    public static final String ENGLISH_TONGA = "en-TO";
    /**
     * en-TT
     */
    public static final String ENGLISH_TRINIDAD_AND_TOBAGO = "en-TT";
    /**
     * en-UG
     */
    public static final String ENGLISH_UGANDA = "en-UG";
    /**
     * en-UM
     */
    public static final String ENGLISH_UNITED_STATES_MINOR_OUTLYING_ISLANDS = "en-UM";
    /**
     * en-US
     */
    public static final String ENGLISH_UNITED_STATES = "en-US";
    /**
     * en-VC
     */
    public static final String ENGLISH_SAINT_VINCENT_AND_THE_GRENADINES = "en-VC";
    /**
     * en-VG
     */
    public static final String ENGLISH_VIRGIN_ISLANDS_BRITISH = "en-VG";
    /**
     * en-VI
     */
    public static final String ENGLISH_VIRGIN_ISLANDS_US = "en-VI";
    /**
     * en-VU
     */
    public static final String ENGLISH_VANUATU = "en-VU";
    /**
     * en-WS
     */
    public static final String ENGLISH_SAMOA = "en-WS";
    /**
     * en-ZA
     */
    public static final String ENGLISH_SOUTH_AFRICA = "en-ZA";
    /**
     * en-ZM
     */
    public static final String ENGLISH_ZAMBIA = "en-ZM";
    /**
     * en-ZW
     */
    public static final String ENGLISH_ZIMBABWE = "en-ZW";
    /**
     * eo
     */
    public static final String ESPERANTO = "eo";
    /**
     * es-AR
     */
    public static final String SPANISH_ARGENTINA = "es-AR";
    /**
     * es-BO
     */
    public static final String SPANISH_BOLIVIA = "es-BO";
    /**
     * es-CL
     */
    public static final String SPANISH_CHILE = "es-CL";
    /**
     * es-CO
     */
    public static final String SPANISH_COLOMBIA = "es-CO";
    /**
     * es-CR
     */
    public static final String SPANISH_COSTA_RICA = "es-CR";
    /**
     * es-CU
     */
    public static final String SPANISH_CUBA = "es-CU";
    /**
     * es-DO
     */
    public static final String SPANISH_DOMINICAN_REPUBLIC = "es-DO";
    /**
     * es-EC
     */
    public static final String SPANISH_ECUADOR = "es-EC";
    /**
     * es-ES
     */
    public static final String SPANISH_SPAIN = "es-ES";
    /**
     * es-GQ
     */
    public static final String SPANISH_EQUATORIAL_GUINEA = "es-GQ";
    /**
     * es-GT
     */
    public static final String SPANISH_GUATEMALA = "es-GT";
    /**
     * es-HN
     */
    public static final String SPANISH_HONDURAS = "es-HN";
    /**
     * es-MX
     */
    public static final String SPANISH_MEXICO = "es-MX";
    /**
     * es-NI
     */
    public static final String SPANISH_NICARAGUA = "es-NI";
    /**
     * es-PA
     */
    public static final String SPANISH_PANAMA = "es-PA";
    /**
     * es-PE
     */
    public static final String SPANISH_PERU = "es-PE";
    /**
     * es-PR
     */
    public static final String SPANISH_PUERTO_RICO = "es-PR";
    /**
     * es-PY
     */
    public static final String SPANISH_PARAGUAY = "es-PY";
    /**
     * es-SV
     */
    public static final String SPANISH_EL_SALVADOR = "es-SV";
    /**
     * es-US
     */
    public static final String SPANISH_UNITED_STATES = "es-US";
    /**
     * es-UY
     */
    public static final String SPANISH_URUGUAY = "es-UY";
    /**
     * es-VE
     */
    public static final String SPANISH_VENEZUELA = "es-VE";
    /**
     * et
     */
    public static final String ESTONIAN = "et";
    /**
     * eu
     */
    public static final String BASQUE = "eu";
    /**
     * fa-AF
     */
    public static final String PERSIAN_AFGHANISTAN = "fa-AF";
    /**
     * fa-IR
     */
    public static final String PERSIAN_IRAN = "fa-IR";
    /**
     * ff-NE
     */
    public static final String FULAH_NIGER = "ff-NE";
    /**
     * ff-NG
     */
    public static final String FULAH_NIGERIA = "ff-NG";
    /**
     * ff-SN
     */
    public static final String FULAH_SENEGAL = "ff-SN";
    /**
     * fi-FI
     */
    public static final String FINNISH_FINLAND = "fi-FI";
    /**
     * fi-SE
     */
    public static final String FINNISH_SWEDEN = "fi-SE";
    /**
     * fj
     */
    public static final String FIJIAN = "fj";
    /**
     * fo
     */
    public static final String FAROESE = "fo";
    /**
     * fr
     */
    public static final String FRENCH_HOLY_SEE_VATICAN_CITY_STATE = "fr";
    /**
     * fr-AD
     */
    public static final String FRENCH_ANDORRA = "fr-AD";
    /**
     * fr-BE
     */
    public static final String FRENCH_BELGIUM = "fr-BE";
    /**
     * fr-BF
     */
    public static final String FRENCH_BURKINA_FASO = "fr-BF";
    /**
     * fr-BI
     */
    public static final String FRENCH_BURUNDI = "fr-BI";
    /**
     * fr-BJ
     */
    public static final String FRENCH_BENIN = "fr-BJ";
    /**
     * fr-CA
     */
    public static final String FRENCH_CANADA = "fr-CA";
    /**
     * fr-CD
     */
    public static final String FRENCH_THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO = "fr-CD";
    /**
     * fr-CF
     */
    public static final String FRENCH_CENTRAL_AFRICAN_REPUBLIC = "fr-CF";
    /**
     * fr-CG
     */
    public static final String FRENCH_CONGO = "fr-CG";
    /**
     * fr-CH
     */
    public static final String FRENCH_SWITZERLAND = "fr-CH";
    /**
     * fr-CI
     */
    public static final String FRENCH_COTE_DIVOIRE = "fr-CI";
    /**
     * fr-CM
     */
    public static final String FRENCH_CAMEROON = "fr-CM";
    /**
     * fr-DJ
     */
    public static final String FRENCH_DJIBOUTI = "fr-DJ";
    /**
     * fr-FR
     */
    public static final String FRENCH_FRANCE = "fr-FR";
    /**
     * fr-GA
     */
    public static final String FRENCH_GABON = "fr-GA";
    /**
     * fr-GB
     */
    public static final String FRENCH_UNITED_KINGDOM = "fr-GB";
    /**
     * fr-GF
     */
    public static final String FRENCH_GUIANA = "fr-GF";
    /**
     * fr-GN
     */
    public static final String FRENCH_GUINEA = "fr-GN";
    /**
     * fr-GP
     */
    public static final String FRENCH_GUADELOUPE = "fr-GP";
    /**
     * fr-HT
     */
    public static final String FRENCH_HAITI = "fr-HT";
    /**
     * fr-IT
     */
    public static final String FRENCH_ITALY = "fr-IT";
    /**
     * fr-KM
     */
    public static final String FRENCH_COMOROS = "fr-KM";
    /**
     * fr-LB
     */
    public static final String FRENCH_LEBANON = "fr-LB";
    /**
     * fr-LU
     */
    public static final String FRENCH_LUXEMBOURG = "fr-LU";
    /**
     * fr-MC
     */
    public static final String FRENCH_MONACO = "fr-MC";
    /**
     * fr-MG
     */
    public static final String FRENCH_MADAGASCAR = "fr-MG";
    /**
     * fr-ML
     */
    public static final String FRENCH_MALI = "fr-ML";
    /**
     * fr-MQ
     */
    public static final String FRENCH_MARTINIQUE = "fr-MQ";
    /**
     * fr-NC
     */
    public static final String FRENCH_NEW_CALEDONIA = "fr-NC";
    /**
     * fr-NE
     */
    public static final String FRENCH_NIGER = "fr-NE";
    /**
     * fr-PF
     */
    public static final String FRENCH_FRENCH_POLYNESIA = "fr-PF";
    /**
     * fr-PM
     */
    public static final String FRENCH_SAINT_PIERRE_AND_MIQUELON = "fr-PM";
    /**
     * fr-RE
     */
    public static final String FRENCH_REUNION = "fr-RE";
    /**
     * fr-RW
     */
    public static final String FRENCH_RWANDA = "fr-RW";
    /**
     * fr-SC
     */
    public static final String FRENCH_SEYCHELLES = "fr-SC";
    /**
     * fr-TD
     */
    public static final String FRENCH_CHAD = "fr-TD";
    /**
     * fr-TG
     */
    public static final String FRENCH_TOGO = "fr-TG";
    /**
     * fr-VU
     */
    public static final String FRENCH_VANUATU = "fr-VU";
    /**
     * fr-WF
     */
    public static final String FRENCH_WALLIS_AND_FUTUNA = "fr-WF";
    /**
     * fr-YT
     */
    public static final String FRENCH_MAYOTTE = "fr-YT";
    /**
     * fy-DE
     */
    public static final String FRISIAN_GERMAN = "fy-DE";
    /**
     * fy-NL
     */
    public static final String FRISIAN_NETHERLANDS = "fy-NL";
    /**
     * ga-GB
     */
    public static final String IRISH_UNITED_KINGDOM = "ga-GB";
    /**
     * ga-IE
     */
    public static final String IRISH_IRELAND = "ga-IE";
    /**
     * gd
     */
    public static final String GAELIC = "gd";
    /**
     * gez-ER
     */
    public static final String GEEZ_ERITREA = "gez-ER";
    /**
     * gez-ET
     */
    public static final String GEEZ_ETHIOPIA = "gez-ET";
    /**
     * gil
     */
    public static final String GILBERTESE = "gil";
    /**
     * gl
     */
    public static final String GALICIAN = "gl";
    /**
     * gn
     */
    public static final String GUARANI = "gn";
    /**
     * gu
     */
    public static final String GUJARATI = "gu";
    /**
     * gv
     */
    public static final String MANX = "gv";
    /**
     * ha
     */
    public static final String HAUSA = "ha";
    /**
     * haw
     */
    public static final String HAWAIIAN = "haw";
    /**
     * he
     */
    public static final String HEBREW = "he";
    /**
     * hi
     */
    public static final String HINDI = "hi";
    /**
     * ho
     */
    public static final String HIRI_MOTU = "ho";
    /**
     * hr-BA
     */
    public static final String CROATIAN_BOSNIA_AND_HERZEGOVINA = "hr-BA";
    /**
     * hr-HR
     */
    public static final String CROATIAN_CROATIA = "hr-HR";
    /**
     * hsb
     */
    public static final String UPPER_SORBIAN = "hsb";
    /**
     * ht
     */
    public static final String HAITIAN = "ht";
    /**
     * hu
     */
    public static final String HUNGARIAN = "hu";
    /**
     * hu-HU
     */
    public static final String HUNGARIAN_HUNGARY = "hu-HU";
    /**
     * hu-SI
     */
    public static final String HUNGARIAN_SLOVENIA = "hu-SI";
    /**
     * hy
     */
    public static final String ARMENIAN = "hy";
    /**
     * hz
     */
    public static final String HERERO = "hz";
    /**
     * ia
     */
    public static final String INTERLINGUA = "ia";
    /**
     * id
     */
    public static final String INDONESIAN = "id";
    /**
     * ie
     */
    public static final String INTERLINGUE = "ie";
    /**
     * ig
     */
    public static final String IGBO = "ig";
    /**
     * ii
     */
    public static final String SICHUAN_YI = "ii";
    /**
     * ik
     */
    public static final String INUPIAQ = "ik";
    /**
     * io
     */
    public static final String IDO = "io";
    /**
     * is
     */
    public static final String ICELANDIC = "is";
    /**
     * it
     */
    public static final String ITALIAN = "it";
    /**
     * it-CH
     */
    public static final String ITALIAN_SWITZERLAND = "it-CH";
    /**
     * it-HR
     */
    public static final String ITALIAN_CROATIA = "it-HR";
    /**
     * it-IT
     */
    public static final String ITALIAN_ITALY = "it-IT";
    /**
     * it-SI
     */
    public static final String ITALIAN_SLOVENIA = "it-SI";
    /**
     * it-SM
     */
    public static final String ITALIAN_SAN_MARINO = "it-SM";
    /**
     * iu
     */
    public static final String INUKTITUT = "iu";
    /**
     * ja
     */
    public static final String JAPANESE = "ja";
    /**
     * jv
     */
    public static final String JAVANESE = "jv";
    /**
     * ka
     */
    public static final String GEORGIAN = "ka";
    /**
     * kg
     */
    public static final String KONGO = "kg";
    /**
     * ki
     */
    public static final String KIKUYU = "ki";
    /**
     * kj
     */
    public static final String KUANYAMA = "kj";
    /**
     * kk
     */
    public static final String KAZAKH = "kk";
    /**
     * kl
     */
    public static final String KALAALLISUT = "kl";
    /**
     * km
     */
    public static final String KHMER = "km";
    /**
     * kn
     */
    public static final String KANNADA = "kn";
    /**
     * ko-KP
     */
    public static final String KOREAN_DEMOCRATIC_PEOPLES_REPUBLIC_OF_KOREA = "ko-KP";
    /**
     * ko-KR
     */
    public static final String KOREAN_REPUBLIC_OF_KOREA = "ko-KR";
    /**
     * kok
     */
    public static final String KONKANI = "kok";
    /**
     * kr
     */
    public static final String KANURI = "kr";
    /**
     * ks
     */
    public static final String KASHMIRI = "ks";
    /**
     * ku
     */
    public static final String KURDISH = "ku";
    /**
     * kv
     */
    public static final String KOMI = "kv";
    /**
     * kw
     */
    public static final String CORNISH = "kw";
    /**
     * ky
     */
    public static final String KIRGHIZ = "ky";
    /**
     * la
     */
    public static final String LATIN = "la";
    /**
     * lb
     */
    public static final String LUXEMBOURGISH = "lb";
    /**
     * lg
     */
    public static final String GANDA = "lg";
    /**
     * li
     */
    public static final String LIMBURGAN = "li";
    /**
     * ln-CD
     */
    public static final String LINGALA_THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO = "ln-CD";
    /**
     * ln-CG
     */
    public static final String LINGALA_CONGO = "ln-CG";
    /**
     * lo
     */
    public static final String LAO = "lo";
    /**
     * lt
     */
    public static final String LITHUANIAN = "lt";
    /**
     * lu
     */
    public static final String LUBA_KATANGA = "lu";
    /**
     * lv
     */
    public static final String LATVIAN = "lv";
    /**
     * mg
     */
    public static final String MALAGASY = "mg";
    /**
     * mh
     */
    public static final String MARSHALLESE = "mh";
    /**
     * mi
     */
    public static final String MAORI = "mi";
    /**
     * mk
     */
    public static final String MACEDONIAN = "mk";
    /**
     * ml
     */
    public static final String MALAYALAM = "ml";
    /**
     * mn
     */
    public static final String MONGOLIAN = "mn";
    /**
     * mo
     */
    public static final String MOLDAVIAN = "mo";
    /**
     * mr
     */
    public static final String MARATHI = "mr";
    /**
     * ms-BN
     */
    public static final String MALAY_BRUNEI_DARUSSALAM = "ms-BN";
    /**
     * ms-CC
     */
    public static final String MALAY_COCOS_KEELING_ISLANDS = "ms-CC";
    /**
     * ms-MY
     */
    public static final String MALAY_MALAYSIA = "ms-MY";
    /**
     * ms-SG
     */
    public static final String MALAY_SINGAPORE = "ms-SG";
    /**
     * mt
     */
    public static final String MALTESE = "mt";
    /**
     * my
     */
    public static final String BURMESE = "my";
    /**
     * na
     */
    public static final String NAURU = "na";
    /**
     * nb
     */
    public static final String BOKMAL = "nb";
    /**
     * nd
     */
    public static final String N_NDEBELE = "nd";
    /**
     * nds
     */
    public static final String LOW_GERMAN = "nds";
    /**
     * ne
     */
    public static final String NEPALI = "ne";
    /**
     * ng
     */
    public static final String NDONGA = "ng";
    /**
     * nl-AN
     */
    public static final String DUTCH_NETHERLANDS_ANTILLES = "nl-AN";
    /**
     * nl-AW
     */
    public static final String DUTCH_ARUBA = "nl-AW";
    /**
     * nl-BE
     */
    public static final String DUTCH_BELGIUM = "nl-BE";
    /**
     * nl-NL
     */
    public static final String DUTCH_NETHERLANDS = "nl-NL";
    /**
     * nl-SR
     */
    public static final String DUTCH_SURINAME = "nl-SR";
    /**
     * nn
     */
    public static final String NYNORSK = "nn";
    /**
     * no
     */
    public static final String NORWEGIAN = "no";
    /**
     * nr
     */
    public static final String S_NDEBELE = "nr";
    /**
     * nv
     */
    public static final String NAVAJO = "nv";
    /**
     * ny
     */
    public static final String CHICHEWA = "ny";
    /**
     * oc
     */
    public static final String OCCITAN = "oc";
    /**
     * oj
     */
    public static final String OJIBWA = "oj";
    /**
     * om-ET
     */
    public static final String OROMO_ETHIOPIA = "om-ET";
    /**
     * om-KE
     */
    public static final String OROMO_KENYA = "om-KE";
    /**
     * or
     */
    public static final String ORIYA = "or";
    /**
     * os
     */
    public static final String OSSETIAN = "os";
    /**
     * pa
     */
    public static final String PANJABI = "pa";
    /**
     * pi
     */
    public static final String PALI = "pi";
    /**
     * pl
     */
    public static final String POLISH = "pl";
    /**
     * ps
     */
    public static final String PUSHTO = "ps";
    /**
     * pt-AO
     */
    public static final String PORTUGUESE_ANGOLA = "pt-AO";
    /**
     * pt-BR
     */
    public static final String PORTUGUESE_BRAZIL = "pt-BR";
    /**
     * pt-CV
     */
    public static final String PORTUGUESE_CAPE_VERDE = "pt-CV";
    /**
     * pt-GW
     */
    public static final String PORTUGUESE_GUINEA_BISSAU = "pt-GW";
    /**
     * pt-MZ
     */
    public static final String PORTUGUESE_MOZAMBIQUE = "pt-MZ";
    /**
     * pt-PT
     */
    public static final String PORTUGUESE_PORTUGAL = "pt-PT";
    /**
     * pt-ST
     */
    public static final String PORTUGUESE_SAO_TOME_AND_PRINCIPE = "pt-ST";
    /**
     * pt-TL
     */
    public static final String PORTUGUESE_TIMOR_LESTE = "pt-TL";
    /**
     * qu
     */
    public static final String QUECHUA = "qu";
    /**
     * rm
     */
    public static final String RAETO_ROMANCE = "rm";
    /**
     * rn
     */
    public static final String RUNDI = "rn";
    /**
     * ro
     */
    public static final String ROMANIAN = "ro";
    /**
     * ru-RU
     */
    public static final String RUSSIAN_RUSSIAN_FEDERATION = "ru-RU";
    /**
     * ru-UA
     */
    public static final String RUSSIAN_UKRAINE = "ru-UA";
    /**
     * rw
     */
    public static final String KINYARWANDA = "rw";
    /**
     * sa
     */
    public static final String SANSKRIT = "sa";
    /**
     * sc
     */
    public static final String SARDINIAN = "sc";
    /**
     * sd-IN
     */
    public static final String SINDHI_INDIA = "sd-IN";
    /**
     * sd-PK
     */
    public static final String SINDHI_PAKISTAN = "sd-PK";
    /**
     * se
     */
    public static final String NORTHERN_SAMI = "se";
    /**
     * sg
     */
    public static final String SANGO = "sg";
    /**
     * si
     */
    public static final String SINHALA = "si";
    /**
     * sid
     */
    public static final String SIDAMO = "sid";
    /**
     * sk
     */
    public static final String SLOVAK = "sk";
    /**
     * sk-HU
     */
    public static final String SLOVAK_HUNGARY = "sk-HU";
    /**
     * sm
     */
    public static final String SAMOAN = "sm";
    /**
     * sma
     */
    public static final String S_SAMI = "sma";
    /**
     * sme
     */
    public static final String N_SAMI = "sme";
    /**
     * smn
     */
    public static final String INARI_SAMI = "smn";
    /**
     * sn
     */
    public static final String SHONA = "sn";
    /**
     * so-DJ
     */
    public static final String SOMALI_DJIBOUTI = "so-DJ";
    /**
     * so-ET
     */
    public static final String SOMALI_ETHIOPIA = "so-ET";
    /**
     * so-KE
     */
    public static final String SOMALI_KENYA = "so-KE";
    /**
     * so-SO
     */
    public static final String SOMALI_SOMALIA = "so-SO";
    /**
     * sq
     */
    public static final String ALBANIAN = "sq";
    /**
     * sr
     */
    public static final String SERBIAN = "sr";
    /**
     * sr-BA
     */
    public static final String SERBIAN_BOSNIA_AND_HERZEGOVINA = "sr-BA";
    /**
     * sr-HU
     */
    public static final String SERBIAN_HUNGARY = "sr-HU";
    /**
     * ss-SZ
     */
    public static final String SWATI_SWAZILAND = "ss-SZ";
    /**
     * ss-ZA
     */
    public static final String SWATI_SOUTH_AFRICA = "ss-ZA";
    /**
     * st
     */
    public static final String SOUTHERN_SOTHO = "st";
    /**
     * su
     */
    public static final String SUNDANESE = "su";
    /**
     * sv-AX
     */
    public static final String SWEDISH_ALAND_ISLANDS = "sv-AX";
    /**
     * sv-FI
     */
    public static final String SWEDISH_FINLAND = "sv-FI";
    /**
     * sv-SE
     */
    public static final String SWEDISH_SWEDEN = "sv-SE";
    /**
     * sw-KE
     */
    public static final String SWAHILI_KENYA = "sw-KE";
    /**
     * sw-TZ
     */
    public static final String SWAHILI_UNITED_REPUBLIC_OF_TANZANIA = "sw-TZ";
    /**
     * syr
     */
    public static final String SYRIAC = "syr";
    /**
     * ta-IN
     */
    public static final String TAMIL_INDIA = "ta-IN";
    /**
     * ta-SG
     */
    public static final String TAMIL_SINGAPORE = "ta-SG";
    /**
     * te
     */
    public static final String TELUGU = "te";
    /**
     * tg
     */
    public static final String TAJIK = "tg";
    /**
     * th
     */
    public static final String THAI = "th";
    /**
     * ti-ER
     */
    public static final String TIGRINYA_ERITREA = "ti-ER";
    /**
     * ti-ET
     */
    public static final String TIGRINYA_ETHIOPIA = "ti-ET";
    /**
     * tig
     */
    public static final String TIGRE = "tig";
    /**
     * tk
     */
    public static final String TURKMEN = "tk";
    /**
     * tl
     */
    public static final String TAGALOG = "tl";
    /**
     * tn-BW
     */
    public static final String TSWANA_TSWANA = "tn-BW";
    /**
     * tn-ZA
     */
    public static final String TSWANA_SOUTH_AFRICA = "tn-ZA";
    /**
     * to
     */
    public static final String TONGAN = "to";
    /**
     * tr
     */
    public static final String TURKISH = "tr";
    /**
     * tr-BG
     */
    public static final String TURKISH_BULGARIA = "tr-BG";
    /**
     * tr-CY
     */
    public static final String TURKISH_CYPRUS = "tr-CY";
    /**
     * tr-TR
     */
    public static final String TURKISH_TURKEY = "tr-TR";
    /**
     * ts
     */
    public static final String TSONGA = "ts";
    /**
     * tt
     */
    public static final String TATAR = "tt";
    /**
     * tvl
     */
    public static final String TUVALU = "tvl";
    /**
     * tw
     */
    public static final String TWI = "tw";
    /**
     * ty
     */
    public static final String TAHITIAN = "ty";
    /**
     * ug
     */
    public static final String UIGHUR = "ug";
    /**
     * uk
     */
    public static final String UKRAINIAN = "uk";
    /**
     * ur-IN
     */
    public static final String URDU_INDIA = "ur-IN";
    /**
     * ur-PK
     */
    public static final String URDU_PAKISTAN = "ur-PK";
    /**
     * uz-AF
     */
    public static final String UZBEK_AFGHANISTAN = "uz-AF";
    /**
     * uz-UZ
     */
    public static final String UZBEK_UZBEKISTAN = "uz-UZ";
    /**
     * ve
     */
    public static final String VENDA = "ve";
    /**
     * vi
     */
    public static final String VIETNAMESE = "vi";
    /**
     * vo
     */
    public static final String VOLAPUK = "vo";
    /**
     * wa
     */
    public static final String WALLOON = "wa";
    /**
     * wal
     */
    public static final String WALAMO = "wal";
    /**
     * wen
     */
    public static final String SORBIAN = "wen";
    /**
     * wo
     */
    public static final String WOLOF = "wo";
    /**
     * xh
     */
    public static final String XHOSA = "xh";
    /**
     * yi
     */
    public static final String YIDDISH = "yi";
    /**
     * yo
     */
    public static final String YORUBA = "yo";
    /**
     * za
     */
    public static final String ZHUANG = "za";
    /**
     * zh-CN
     */
    public static final String CHINESE_CHINA = "zh-CN";
    /**
     * zh-HK
     */
    public static final String CHINESE_HONG_KONG = "zh-HK";
    /**
     * zh-Hans-SG
     */
    public static final String CHINESE_TRADITIONAL_SINGAPORE = "zh-Hans-SG";
    /**
     * zh-Hant-HK
     */
    public static final String CHINESE_TRADITIONAL_HONG_KONG = "zh-Hant-HK";
    /**
     * zh-MO
     */
    public static final String CHINESE_MACAO = "zh-MO";
    /**
     * zh-SG
     */
    public static final String CHINESE_SINGAPORE = "zh-SG";
    /**
     * zh-TW
     */
    public static final String CHINESE_TAIWAN = "zh-TW";
    /**
     * zu
     */
    public static final String ZULU = "zu";

    /**
     * Locale references.
     */
    private static HashMap<String, String> mLocales = new HashMap<>();

    static {
        mLocales.put(AFAR_DJIBOUTI, AFAR_DJIBOUTI);
        mLocales.put(AFAR_ERITREA, AFAR_ERITREA);
        mLocales.put(AFAR_ETHIOPIA, AFAR_ETHIOPIA);
        mLocales.put(ABKHAZIAN, ABKHAZIAN);
        mLocales.put(AVESTAN, AVESTAN);
        mLocales.put(AFRIKAANS, AFRIKAANS);
        mLocales.put(AKAN, AKAN);
        mLocales.put(AMHARIC, AMHARIC);
        mLocales.put(ARAGONESE, ARAGONESE);
        mLocales.put(ARABIC_UNITED_ARAB_EMIRATES, ARABIC_UNITED_ARAB_EMIRATES);
        mLocales.put(ARABIC_BAHRAIN, ARABIC_BAHRAIN);
        mLocales.put(ARABIC_ALGERIA, ARABIC_ALGERIA);
        mLocales.put(ARABIC_EGYPT, ARABIC_EGYPT);
        mLocales.put(ARABIC_ISRAEL, ARABIC_ISRAEL);
        mLocales.put(ARABIC_INDIA, ARABIC_INDIA);
        mLocales.put(ARABIC_IRAQ, ARABIC_IRAQ);
        mLocales.put(ARABIC_JORDAN, ARABIC_JORDAN);
        mLocales.put(ARABIC_KUWAIT, ARABIC_KUWAIT);
        mLocales.put(ARABIC_LEBANON, ARABIC_LEBANON);
        mLocales.put(ARABIC_LIBYAN_ARAB_JAMAHIRIYA, ARABIC_LIBYAN_ARAB_JAMAHIRIYA);
        mLocales.put(ARABIC_MOROCCO, ARABIC_MOROCCO);
        mLocales.put(ARABIC_MAURITANIA, ARABIC_MAURITANIA);
        mLocales.put(ARABIC_OMAN, ARABIC_OMAN);
        mLocales.put(ARABIC_PALESTINIAN_TERRITORY, ARABIC_PALESTINIAN_TERRITORY);
        mLocales.put(ARABIC_QATAR, ARABIC_QATAR);
        mLocales.put(ARABIC_SAUDI_ARABIA, ARABIC_SAUDI_ARABIA);
        mLocales.put(ARABIC_SUDAN, ARABIC_SUDAN);
        mLocales.put(ARABIC_SOMALIA, ARABIC_SOMALIA);
        mLocales.put(ARABIC_SYRIAN_ARAB_REPUBLIC, ARABIC_SYRIAN_ARAB_REPUBLIC);
        mLocales.put(ARABIC_CHAD, ARABIC_CHAD);
        mLocales.put(ARABIC_TUNISIA, ARABIC_TUNISIA);
        mLocales.put(ARABIC_YEMEN, ARABIC_YEMEN);
        mLocales.put(ASSAMESE, ASSAMESE);
        mLocales.put(AVARIC, AVARIC);
        mLocales.put(AYMARA, AYMARA);
        mLocales.put(AZERBAIJANI, AZERBAIJANI);
        mLocales.put(BASHKIR, BASHKIR);
        mLocales.put(BELARUSIAN, BELARUSIAN);
        mLocales.put(BULGARIAN, BULGARIAN);
        mLocales.put(BIHARI, BIHARI);
        mLocales.put(BISLAMA, BISLAMA);
        mLocales.put(BAMBARA, BAMBARA);
        mLocales.put(BENGALI_BANGLADESH, BENGALI_BANGLADESH);
        mLocales.put(BENGALI_INDIA, BENGALI_INDIA);
        mLocales.put(BENGALI_SINGAPORE, BENGALI_SINGAPORE);
        mLocales.put(TIBETAN, TIBETAN);
        mLocales.put(BRETON, BRETON);
        mLocales.put(BOSNIAN, BOSNIAN);
        mLocales.put(BLIN, BLIN);
        mLocales.put(CATALAN, CATALAN);
        mLocales.put(CHECHEN, CHECHEN);
        mLocales.put(CHAMORRO_GUAM, CHAMORRO_GUAM);
        mLocales.put(CHAMORRO_NORTHERN_MARIANA_ISLANDS, CHAMORRO_NORTHERN_MARIANA_ISLANDS);
        mLocales.put(CORSICAN, CORSICAN);
        mLocales.put(CREE, CREE);
        mLocales.put(CZECH, CZECH);
        mLocales.put(CHURCH_SLAVIC, CHURCH_SLAVIC);
        mLocales.put(CHUVASH, CHUVASH);
        mLocales.put(WELSH_ARGENTINA, WELSH_ARGENTINA);
        mLocales.put(WELSH_UNITED_KINGDOM, WELSH_UNITED_KINGDOM);
        mLocales.put(DANISH_GERMANY, DANISH_GERMANY);
        mLocales.put(DANISH_DENMARK, DANISH_DENMARK);
        mLocales.put(DANISH_FAROE_ISLANDS, DANISH_FAROE_ISLANDS);
        mLocales.put(DANISH_GREENLAND, DANISH_GREENLAND);
        mLocales.put(GERMAN_AUSTRIA, GERMAN_AUSTRIA);
        mLocales.put(GERMAN_BELGIUM, GERMAN_BELGIUM);
        mLocales.put(GERMAN_SWITZERLAND, GERMAN_SWITZERLAND);
        mLocales.put(GERMAN_GERMANY, GERMAN_GERMANY);
        mLocales.put(GERMAN_DENMARK, GERMAN_DENMARK);
        mLocales.put(GERMAN_FRANCE, GERMAN_FRANCE);
        mLocales.put(GERMAN_HUNGARY, GERMAN_HUNGARY);
        mLocales.put(GERMAN_ITALY, GERMAN_ITALY);
        mLocales.put(GERMAN_LIECHTENSTEIN, GERMAN_LIECHTENSTEIN);
        mLocales.put(GERMAN_LUXEMBOURG, GERMAN_LUXEMBOURG);
        mLocales.put(GERMAN_POLAND, GERMAN_POLAND);
        mLocales.put(DINKA, DINKA);
        mLocales.put(LOWER_SORBIAN, LOWER_SORBIAN);
        mLocales.put(DIVEHI, DIVEHI);
        mLocales.put(DZONGKHA, DZONGKHA);
        mLocales.put(EWE, EWE);
        mLocales.put(GREEK_CYPRUS, GREEK_CYPRUS);
        mLocales.put(GREEK_GREECE, GREEK_GREECE);
        mLocales.put(ENGLISH_ANTIGUA_AND_BARBUDA, ENGLISH_ANTIGUA_AND_BARBUDA);
        mLocales.put(ENGLISH_ANGUILLA, ENGLISH_ANGUILLA);
        mLocales.put(ENGLISH_AMERICAN_SAMOA, ENGLISH_AMERICAN_SAMOA);
        mLocales.put(ENGLISH_AUSTRALIA, ENGLISH_AUSTRALIA);
        mLocales.put(ENGLISH_BARBADOS, ENGLISH_BARBADOS);
        mLocales.put(ENGLISH_BELGIUM, ENGLISH_BELGIUM);
        mLocales.put(ENGLISH_BERMUDA, ENGLISH_BERMUDA);
        mLocales.put(ENGLISH_BRUNEI_DARUSSALAM, ENGLISH_BRUNEI_DARUSSALAM);
        mLocales.put(ENGLISH_BAHAMAS, ENGLISH_BAHAMAS);
        mLocales.put(ENGLISH_BOTSWANA, ENGLISH_BOTSWANA);
        mLocales.put(ENGLISH_BELIZE, ENGLISH_BELIZE);
        mLocales.put(ENGLISH_CANADA, ENGLISH_CANADA);
        mLocales.put(ENGLISH_COOK_ISLANDS, ENGLISH_COOK_ISLANDS);
        mLocales.put(ENGLISH_CAMEROON, ENGLISH_CAMEROON);
        mLocales.put(ENGLISH_DOMINICA, ENGLISH_DOMINICA);
        mLocales.put(ENGLISH_ERITREA, ENGLISH_ERITREA);
        mLocales.put(ENGLISH_ETHIOPIA, ENGLISH_ETHIOPIA);
        mLocales.put(ENGLISH_FIJI, ENGLISH_FIJI);
        mLocales.put(ENGLISH_FALKLAND_ISLANDS_MALVINAS, ENGLISH_FALKLAND_ISLANDS_MALVINAS);
        mLocales.put(ENGLISH_MICRONESIA, ENGLISH_MICRONESIA);
        mLocales.put(ENGLISH_UNITED_KINGDOM, ENGLISH_UNITED_KINGDOM);
        mLocales.put(ENGLISH_GRENADA, ENGLISH_GRENADA);
        mLocales.put(ENGLISH_GHANA, ENGLISH_GHANA);
        mLocales.put(ENGLISH_GIBRALTAR, ENGLISH_GIBRALTAR);
        mLocales.put(ENGLISH_GAMBIA, ENGLISH_GAMBIA);
        mLocales.put(ENGLISH_GUAM, ENGLISH_GUAM);
        mLocales.put(ENGLISH_GUYANA, ENGLISH_GUYANA);
        mLocales.put(ENGLISH_HONG_KONG, ENGLISH_HONG_KONG);
        mLocales.put(ENGLISH_IRELAND, ENGLISH_IRELAND);
        mLocales.put(ENGLISH_ISRAEL, ENGLISH_ISRAEL);
        mLocales.put(ENGLISH_INDIA, ENGLISH_INDIA);
        mLocales.put(ENGLISH_BRITISH_INDIAN_OCEAN_TERRITORY, ENGLISH_BRITISH_INDIAN_OCEAN_TERRITORY);
        mLocales.put(ENGLISH_JAMAICA, ENGLISH_JAMAICA);
        mLocales.put(ENGLISH_KENYA, ENGLISH_KENYA);
        mLocales.put(ENGLISH_KIRIBATI, ENGLISH_KIRIBATI);
        mLocales.put(ENGLISH_SAINT_KITTS_AND_NEVIS, ENGLISH_SAINT_KITTS_AND_NEVIS);
        mLocales.put(ENGLISH_CAYMAN_ISLANDS, ENGLISH_CAYMAN_ISLANDS);
        mLocales.put(ENGLISH_SAINT_LUCIA, ENGLISH_SAINT_LUCIA);
        mLocales.put(ENGLISH_LIBERIA, ENGLISH_LIBERIA);
        mLocales.put(ENGLISH_LESOTHO, ENGLISH_LESOTHO);
        mLocales.put(ENGLISH_MARSHALL_ISLANDS, ENGLISH_MARSHALL_ISLANDS);
        mLocales.put(ENGLISH_NORTHERN_MARIANA_ISLANDS, ENGLISH_NORTHERN_MARIANA_ISLANDS);
        mLocales.put(ENGLISH_MONTSERRAT, ENGLISH_MONTSERRAT);
        mLocales.put(ENGLISH_MALTA, ENGLISH_MALTA);
        mLocales.put(ENGLISH_MAURITIUS, ENGLISH_MAURITIUS);
        mLocales.put(ENGLISH_MALAWI, ENGLISH_MALAWI);
        mLocales.put(ENGLISH_NAMIBIA, ENGLISH_NAMIBIA);
        mLocales.put(ENGLISH_NORFOLK_ISLAND, ENGLISH_NORFOLK_ISLAND);
        mLocales.put(ENGLISH_NIGERIA, ENGLISH_NIGERIA);
        mLocales.put(ENGLISH_NAURU, ENGLISH_NAURU);
        mLocales.put(ENGLISH_NIUE, ENGLISH_NIUE);
        mLocales.put(ENGLISH_NEW_ZEALAND, ENGLISH_NEW_ZEALAND);
        mLocales.put(ENGLISH_PAPUA_NEW_GUINEA, ENGLISH_PAPUA_NEW_GUINEA);
        mLocales.put(ENGLISH_PHILIPPINES, ENGLISH_PHILIPPINES);
        mLocales.put(ENGLISH_PAKISTAN, ENGLISH_PAKISTAN);
        mLocales.put(ENGLISH_PITCAIRN, ENGLISH_PITCAIRN);
        mLocales.put(ENGLISH_PUERTO_RICO, ENGLISH_PUERTO_RICO);
        mLocales.put(ENGLISH_PALAU, ENGLISH_PALAU);
        mLocales.put(ENGLISH_RWANDA, ENGLISH_RWANDA);
        mLocales.put(ENGLISH_SOLOMON_ISLANDS, ENGLISH_SOLOMON_ISLANDS);
        mLocales.put(ENGLISH_SEYCHELLES, ENGLISH_SEYCHELLES);
        mLocales.put(ENGLISH_SINGAPORE, ENGLISH_SINGAPORE);
        mLocales.put(ENGLISH_SAINT_HELENA, ENGLISH_SAINT_HELENA);
        mLocales.put(ENGLISH_SIERRA_LEONE, ENGLISH_SIERRA_LEONE);
        mLocales.put(ENGLISH_SOMALIA, ENGLISH_SOMALIA);
        mLocales.put(ENGLISH_SWAZILAND, ENGLISH_SWAZILAND);
        mLocales.put(ENGLISH_TURKS_AND_CAICOS_ISLANDS, ENGLISH_TURKS_AND_CAICOS_ISLANDS);
        mLocales.put(ENGLISH_TOKELAU, ENGLISH_TOKELAU);
        mLocales.put(ENGLISH_TONGA, ENGLISH_TONGA);
        mLocales.put(ENGLISH_TRINIDAD_AND_TOBAGO, ENGLISH_TRINIDAD_AND_TOBAGO);
        mLocales.put(ENGLISH_UGANDA, ENGLISH_UGANDA);
        mLocales.put(ENGLISH_UNITED_STATES_MINOR_OUTLYING_ISLANDS, ENGLISH_UNITED_STATES_MINOR_OUTLYING_ISLANDS);
        mLocales.put(ENGLISH_UNITED_STATES, ENGLISH_UNITED_STATES);
        mLocales.put(ENGLISH_SAINT_VINCENT_AND_THE_GRENADINES, ENGLISH_SAINT_VINCENT_AND_THE_GRENADINES);
        mLocales.put(ENGLISH_VIRGIN_ISLANDS_BRITISH, ENGLISH_VIRGIN_ISLANDS_BRITISH);
        mLocales.put(ENGLISH_VIRGIN_ISLANDS_US, ENGLISH_VIRGIN_ISLANDS_US);
        mLocales.put(ENGLISH_VANUATU, ENGLISH_VANUATU);
        mLocales.put(ENGLISH_SAMOA, ENGLISH_SAMOA);
        mLocales.put(ENGLISH_SOUTH_AFRICA, ENGLISH_SOUTH_AFRICA);
        mLocales.put(ENGLISH_ZAMBIA, ENGLISH_ZAMBIA);
        mLocales.put(ENGLISH_ZIMBABWE, ENGLISH_ZIMBABWE);
        mLocales.put(ESPERANTO, ESPERANTO);
        mLocales.put(SPANISH_ARGENTINA, SPANISH_ARGENTINA);
        mLocales.put(SPANISH_BOLIVIA, SPANISH_BOLIVIA);
        mLocales.put(SPANISH_CHILE, SPANISH_CHILE);
        mLocales.put(SPANISH_COLOMBIA, SPANISH_COLOMBIA);
        mLocales.put(SPANISH_COSTA_RICA, SPANISH_COSTA_RICA);
        mLocales.put(SPANISH_CUBA, SPANISH_CUBA);
        mLocales.put(SPANISH_DOMINICAN_REPUBLIC, SPANISH_DOMINICAN_REPUBLIC);
        mLocales.put(SPANISH_ECUADOR, SPANISH_ECUADOR);
        mLocales.put(SPANISH_SPAIN, SPANISH_SPAIN);
        mLocales.put(SPANISH_EQUATORIAL_GUINEA, SPANISH_EQUATORIAL_GUINEA);
        mLocales.put(SPANISH_GUATEMALA, SPANISH_GUATEMALA);
        mLocales.put(SPANISH_HONDURAS, SPANISH_HONDURAS);
        mLocales.put(SPANISH_MEXICO, SPANISH_MEXICO);
        mLocales.put(SPANISH_NICARAGUA, SPANISH_NICARAGUA);
        mLocales.put(SPANISH_PANAMA, SPANISH_PANAMA);
        mLocales.put(SPANISH_PERU, SPANISH_PERU);
        mLocales.put(SPANISH_PUERTO_RICO, SPANISH_PUERTO_RICO);
        mLocales.put(SPANISH_PARAGUAY, SPANISH_PARAGUAY);
        mLocales.put(SPANISH_EL_SALVADOR, SPANISH_EL_SALVADOR);
        mLocales.put(SPANISH_UNITED_STATES, SPANISH_UNITED_STATES);
        mLocales.put(SPANISH_URUGUAY, SPANISH_URUGUAY);
        mLocales.put(SPANISH_VENEZUELA, SPANISH_VENEZUELA);
        mLocales.put(ESTONIAN, ESTONIAN);
        mLocales.put(BASQUE, BASQUE);
        mLocales.put(PERSIAN_AFGHANISTAN, PERSIAN_AFGHANISTAN);
        mLocales.put(PERSIAN_IRAN, PERSIAN_IRAN);
        mLocales.put(FULAH_NIGER, FULAH_NIGER);
        mLocales.put(FULAH_NIGERIA, FULAH_NIGERIA);
        mLocales.put(FULAH_SENEGAL, FULAH_SENEGAL);
        mLocales.put(FINNISH_FINLAND, FINNISH_FINLAND);
        mLocales.put(FINNISH_SWEDEN, FINNISH_SWEDEN);
        mLocales.put(FIJIAN, FIJIAN);
        mLocales.put(FAROESE, FAROESE);
        mLocales.put(FRENCH_HOLY_SEE_VATICAN_CITY_STATE, FRENCH_HOLY_SEE_VATICAN_CITY_STATE);
        mLocales.put(FRENCH_ANDORRA, FRENCH_ANDORRA);
        mLocales.put(FRENCH_BELGIUM, FRENCH_BELGIUM);
        mLocales.put(FRENCH_BURKINA_FASO, FRENCH_BURKINA_FASO);
        mLocales.put(FRENCH_BURUNDI, FRENCH_BURUNDI);
        mLocales.put(FRENCH_BENIN, FRENCH_BENIN);
        mLocales.put(FRENCH_CANADA, FRENCH_CANADA);
        mLocales.put(FRENCH_THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO, FRENCH_THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO);
        mLocales.put(FRENCH_CENTRAL_AFRICAN_REPUBLIC, FRENCH_CENTRAL_AFRICAN_REPUBLIC);
        mLocales.put(FRENCH_CONGO, FRENCH_CONGO);
        mLocales.put(FRENCH_SWITZERLAND, FRENCH_SWITZERLAND);
        mLocales.put(FRENCH_COTE_DIVOIRE, FRENCH_COTE_DIVOIRE);
        mLocales.put(FRENCH_CAMEROON, FRENCH_CAMEROON);
        mLocales.put(FRENCH_DJIBOUTI, FRENCH_DJIBOUTI);
        mLocales.put(FRENCH_FRANCE, FRENCH_FRANCE);
        mLocales.put(FRENCH_GABON, FRENCH_GABON);
        mLocales.put(FRENCH_UNITED_KINGDOM, FRENCH_UNITED_KINGDOM);
        mLocales.put(FRENCH_GUIANA, FRENCH_GUIANA);
        mLocales.put(FRENCH_GUINEA, FRENCH_GUINEA);
        mLocales.put(FRENCH_GUADELOUPE, FRENCH_GUADELOUPE);
        mLocales.put(FRENCH_HAITI, FRENCH_HAITI);
        mLocales.put(FRENCH_ITALY, FRENCH_ITALY);
        mLocales.put(FRENCH_COMOROS, FRENCH_COMOROS);
        mLocales.put(FRENCH_LEBANON, FRENCH_LEBANON);
        mLocales.put(FRENCH_LUXEMBOURG, FRENCH_LUXEMBOURG);
        mLocales.put(FRENCH_MONACO, FRENCH_MONACO);
        mLocales.put(FRENCH_MADAGASCAR, FRENCH_MADAGASCAR);
        mLocales.put(FRENCH_MALI, FRENCH_MALI);
        mLocales.put(FRENCH_MARTINIQUE, FRENCH_MARTINIQUE);
        mLocales.put(FRENCH_NEW_CALEDONIA, FRENCH_NEW_CALEDONIA);
        mLocales.put(FRENCH_NIGER, FRENCH_NIGER);
        mLocales.put(FRENCH_FRENCH_POLYNESIA, FRENCH_FRENCH_POLYNESIA);
        mLocales.put(FRENCH_SAINT_PIERRE_AND_MIQUELON, FRENCH_SAINT_PIERRE_AND_MIQUELON);
        mLocales.put(FRENCH_REUNION, FRENCH_REUNION);
        mLocales.put(FRENCH_RWANDA, FRENCH_RWANDA);
        mLocales.put(FRENCH_SEYCHELLES, FRENCH_SEYCHELLES);
        mLocales.put(FRENCH_CHAD, FRENCH_CHAD);
        mLocales.put(FRENCH_TOGO, FRENCH_TOGO);
        mLocales.put(FRENCH_VANUATU, FRENCH_VANUATU);
        mLocales.put(FRENCH_WALLIS_AND_FUTUNA, FRENCH_WALLIS_AND_FUTUNA);
        mLocales.put(FRENCH_MAYOTTE, FRENCH_MAYOTTE);
        mLocales.put(FRISIAN_GERMAN, FRISIAN_GERMAN);
        mLocales.put(FRISIAN_NETHERLANDS, FRISIAN_NETHERLANDS);
        mLocales.put(IRISH_UNITED_KINGDOM, IRISH_UNITED_KINGDOM);
        mLocales.put(IRISH_IRELAND, IRISH_IRELAND);
        mLocales.put(GAELIC, GAELIC);
        mLocales.put(GEEZ_ERITREA, GEEZ_ERITREA);
        mLocales.put(GEEZ_ETHIOPIA, GEEZ_ETHIOPIA);
        mLocales.put(GILBERTESE, GILBERTESE);
        mLocales.put(GALICIAN, GALICIAN);
        mLocales.put(GUARANI, GUARANI);
        mLocales.put(GUJARATI, GUJARATI);
        mLocales.put(MANX, MANX);
        mLocales.put(HAUSA, HAUSA);
        mLocales.put(HAWAIIAN, HAWAIIAN);
        mLocales.put(HEBREW, HEBREW);
        mLocales.put(HINDI, HINDI);
        mLocales.put(HIRI_MOTU, HIRI_MOTU);
        mLocales.put(CROATIAN_BOSNIA_AND_HERZEGOVINA, CROATIAN_BOSNIA_AND_HERZEGOVINA);
        mLocales.put(CROATIAN_CROATIA, CROATIAN_CROATIA);
        mLocales.put(UPPER_SORBIAN, UPPER_SORBIAN);
        mLocales.put(HAITIAN, HAITIAN);
        mLocales.put(HUNGARIAN, HUNGARIAN);
        mLocales.put(HUNGARIAN_HUNGARY, HUNGARIAN_HUNGARY);
        mLocales.put(HUNGARIAN_SLOVENIA, HUNGARIAN_SLOVENIA);
        mLocales.put(ARMENIAN, ARMENIAN);
        mLocales.put(HERERO, HERERO);
        mLocales.put(INTERLINGUA, INTERLINGUA);
        mLocales.put(INDONESIAN, INDONESIAN);
        mLocales.put(INTERLINGUE, INTERLINGUE);
        mLocales.put(IGBO, IGBO);
        mLocales.put(SICHUAN_YI, SICHUAN_YI);
        mLocales.put(INUPIAQ, INUPIAQ);
        mLocales.put(IDO, IDO);
        mLocales.put(ICELANDIC, ICELANDIC);
        mLocales.put(ITALIAN, ITALIAN);
        mLocales.put(ITALIAN_SWITZERLAND, ITALIAN_SWITZERLAND);
        mLocales.put(ITALIAN_CROATIA, ITALIAN_CROATIA);
        mLocales.put(ITALIAN_ITALY, ITALIAN_ITALY);
        mLocales.put(ITALIAN_SLOVENIA, ITALIAN_SLOVENIA);
        mLocales.put(ITALIAN_SAN_MARINO, ITALIAN_SAN_MARINO);
        mLocales.put(INUKTITUT, INUKTITUT);
        mLocales.put(JAPANESE, JAPANESE);
        mLocales.put(JAVANESE, JAVANESE);
        mLocales.put(GEORGIAN, GEORGIAN);
        mLocales.put(KONGO, KONGO);
        mLocales.put(KIKUYU, KIKUYU);
        mLocales.put(KUANYAMA, KUANYAMA);
        mLocales.put(KAZAKH, KAZAKH);
        mLocales.put(KALAALLISUT, KALAALLISUT);
        mLocales.put(KHMER, KHMER);
        mLocales.put(KANNADA, KANNADA);
        mLocales.put(KOREAN_DEMOCRATIC_PEOPLES_REPUBLIC_OF_KOREA, KOREAN_DEMOCRATIC_PEOPLES_REPUBLIC_OF_KOREA);
        mLocales.put(KOREAN_REPUBLIC_OF_KOREA, KOREAN_REPUBLIC_OF_KOREA);
        mLocales.put(KONKANI, KONKANI);
        mLocales.put(KANURI, KANURI);
        mLocales.put(KASHMIRI, KASHMIRI);
        mLocales.put(KURDISH, KURDISH);
        mLocales.put(KOMI, KOMI);
        mLocales.put(CORNISH, CORNISH);
        mLocales.put(KIRGHIZ, KIRGHIZ);
        mLocales.put(LATIN, LATIN);
        mLocales.put(LUXEMBOURGISH, LUXEMBOURGISH);
        mLocales.put(GANDA, GANDA);
        mLocales.put(LIMBURGAN, LIMBURGAN);
        mLocales.put(LINGALA_THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO, LINGALA_THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO);
        mLocales.put(LINGALA_CONGO, LINGALA_CONGO);
        mLocales.put(LAO, LAO);
        mLocales.put(LITHUANIAN, LITHUANIAN);
        mLocales.put(LUBA_KATANGA, LUBA_KATANGA);
        mLocales.put(LATVIAN, LATVIAN);
        mLocales.put(MALAGASY, MALAGASY);
        mLocales.put(MARSHALLESE, MARSHALLESE);
        mLocales.put(MAORI, MAORI);
        mLocales.put(MACEDONIAN, MACEDONIAN);
        mLocales.put(MALAYALAM, MALAYALAM);
        mLocales.put(MONGOLIAN, MONGOLIAN);
        mLocales.put(MOLDAVIAN, MOLDAVIAN);
        mLocales.put(MARATHI, MARATHI);
        mLocales.put(MALAY_BRUNEI_DARUSSALAM, MALAY_BRUNEI_DARUSSALAM);
        mLocales.put(MALAY_COCOS_KEELING_ISLANDS, MALAY_COCOS_KEELING_ISLANDS);
        mLocales.put(MALAY_MALAYSIA, MALAY_MALAYSIA);
        mLocales.put(MALAY_SINGAPORE, MALAY_SINGAPORE);
        mLocales.put(MALTESE, MALTESE);
        mLocales.put(BURMESE, BURMESE);
        mLocales.put(NAURU, NAURU);
        mLocales.put(BOKMAL, BOKMAL);
        mLocales.put(N_NDEBELE, N_NDEBELE);
        mLocales.put(LOW_GERMAN, LOW_GERMAN);
        mLocales.put(NEPALI, NEPALI);
        mLocales.put(NDONGA, NDONGA);
        mLocales.put(DUTCH_NETHERLANDS_ANTILLES, DUTCH_NETHERLANDS_ANTILLES);
        mLocales.put(DUTCH_ARUBA, DUTCH_ARUBA);
        mLocales.put(DUTCH_BELGIUM, DUTCH_BELGIUM);
        mLocales.put(DUTCH_NETHERLANDS, DUTCH_NETHERLANDS);
        mLocales.put(DUTCH_SURINAME, DUTCH_SURINAME);
        mLocales.put(NYNORSK, NYNORSK);
        mLocales.put(NORWEGIAN, NORWEGIAN);
        mLocales.put(S_NDEBELE, S_NDEBELE);
        mLocales.put(NAVAJO, NAVAJO);
        mLocales.put(CHICHEWA, CHICHEWA);
        mLocales.put(OCCITAN, OCCITAN);
        mLocales.put(OJIBWA, OJIBWA);
        mLocales.put(OROMO_ETHIOPIA, OROMO_ETHIOPIA);
        mLocales.put(OROMO_KENYA, OROMO_KENYA);
        mLocales.put(ORIYA, ORIYA);
        mLocales.put(OSSETIAN, OSSETIAN);
        mLocales.put(PANJABI, PANJABI);
        mLocales.put(PALI, PALI);
        mLocales.put(POLISH, POLISH);
        mLocales.put(PUSHTO, PUSHTO);
        mLocales.put(PORTUGUESE_ANGOLA, PORTUGUESE_ANGOLA);
        mLocales.put(PORTUGUESE_BRAZIL, PORTUGUESE_BRAZIL);
        mLocales.put(PORTUGUESE_CAPE_VERDE, PORTUGUESE_CAPE_VERDE);
        mLocales.put(PORTUGUESE_GUINEA_BISSAU, PORTUGUESE_GUINEA_BISSAU);
        mLocales.put(PORTUGUESE_MOZAMBIQUE, PORTUGUESE_MOZAMBIQUE);
        mLocales.put(PORTUGUESE_PORTUGAL, PORTUGUESE_PORTUGAL);
        mLocales.put(PORTUGUESE_SAO_TOME_AND_PRINCIPE, PORTUGUESE_SAO_TOME_AND_PRINCIPE);
        mLocales.put(PORTUGUESE_TIMOR_LESTE, PORTUGUESE_TIMOR_LESTE);
        mLocales.put(QUECHUA, QUECHUA);
        mLocales.put(RAETO_ROMANCE, RAETO_ROMANCE);
        mLocales.put(RUNDI, RUNDI);
        mLocales.put(ROMANIAN, ROMANIAN);
        mLocales.put(RUSSIAN_RUSSIAN_FEDERATION, RUSSIAN_RUSSIAN_FEDERATION);
        mLocales.put(RUSSIAN_UKRAINE, RUSSIAN_UKRAINE);
        mLocales.put(KINYARWANDA, KINYARWANDA);
        mLocales.put(SANSKRIT, SANSKRIT);
        mLocales.put(SARDINIAN, SARDINIAN);
        mLocales.put(SINDHI_INDIA, SINDHI_INDIA);
        mLocales.put(SINDHI_PAKISTAN, SINDHI_PAKISTAN);
        mLocales.put(NORTHERN_SAMI, NORTHERN_SAMI);
        mLocales.put(SANGO, SANGO);
        mLocales.put(SINHALA, SINHALA);
        mLocales.put(SIDAMO, SIDAMO);
        mLocales.put(SLOVAK, SLOVAK);
        mLocales.put(SLOVAK_HUNGARY, SLOVAK_HUNGARY);
        mLocales.put(SAMOAN, SAMOAN);
        mLocales.put(S_SAMI, S_SAMI);
        mLocales.put(N_SAMI, N_SAMI);
        mLocales.put(INARI_SAMI, INARI_SAMI);
        mLocales.put(SHONA, SHONA);
        mLocales.put(SOMALI_DJIBOUTI, SOMALI_DJIBOUTI);
        mLocales.put(SOMALI_ETHIOPIA, SOMALI_ETHIOPIA);
        mLocales.put(SOMALI_KENYA, SOMALI_KENYA);
        mLocales.put(SOMALI_SOMALIA, SOMALI_SOMALIA);
        mLocales.put(ALBANIAN, ALBANIAN);
        mLocales.put(SERBIAN, SERBIAN);
        mLocales.put(SERBIAN_BOSNIA_AND_HERZEGOVINA, SERBIAN_BOSNIA_AND_HERZEGOVINA);
        mLocales.put(SERBIAN_HUNGARY, SERBIAN_HUNGARY);
        mLocales.put(SWATI_SWAZILAND, SWATI_SWAZILAND);
        mLocales.put(SWATI_SOUTH_AFRICA, SWATI_SOUTH_AFRICA);
        mLocales.put(SOUTHERN_SOTHO, SOUTHERN_SOTHO);
        mLocales.put(SUNDANESE, SUNDANESE);
        mLocales.put(SWEDISH_ALAND_ISLANDS, SWEDISH_ALAND_ISLANDS);
        mLocales.put(SWEDISH_FINLAND, SWEDISH_FINLAND);
        mLocales.put(SWEDISH_SWEDEN, SWEDISH_SWEDEN);
        mLocales.put(SWAHILI_KENYA, SWAHILI_KENYA);
        mLocales.put(SWAHILI_UNITED_REPUBLIC_OF_TANZANIA, SWAHILI_UNITED_REPUBLIC_OF_TANZANIA);
        mLocales.put(SYRIAC, SYRIAC);
        mLocales.put(TAMIL_INDIA, TAMIL_INDIA);
        mLocales.put(TAMIL_SINGAPORE, TAMIL_SINGAPORE);
        mLocales.put(TELUGU, TELUGU);
        mLocales.put(TAJIK, TAJIK);
        mLocales.put(THAI, THAI);
        mLocales.put(TIGRINYA_ERITREA, TIGRINYA_ERITREA);
        mLocales.put(TIGRINYA_ETHIOPIA, TIGRINYA_ETHIOPIA);
        mLocales.put(TIGRE, TIGRE);
        mLocales.put(TURKMEN, TURKMEN);
        mLocales.put(TAGALOG, TAGALOG);
        mLocales.put(TSWANA_TSWANA, TSWANA_TSWANA);
        mLocales.put(TSWANA_SOUTH_AFRICA, TSWANA_SOUTH_AFRICA);
        mLocales.put(TONGAN, TONGAN);
        mLocales.put(TURKISH, TURKISH);
        mLocales.put(TURKISH_BULGARIA, TURKISH_BULGARIA);
        mLocales.put(TURKISH_CYPRUS, TURKISH_CYPRUS);
        mLocales.put(TURKISH_TURKEY, TURKISH_TURKEY);
        mLocales.put(TSONGA, TSONGA);
        mLocales.put(TATAR, TATAR);
        mLocales.put(TUVALU, TUVALU);
        mLocales.put(TWI, TWI);
        mLocales.put(TAHITIAN, TAHITIAN);
        mLocales.put(UIGHUR, UIGHUR);
        mLocales.put(UKRAINIAN, UKRAINIAN);
        mLocales.put(URDU_INDIA, URDU_INDIA);
        mLocales.put(URDU_PAKISTAN, URDU_PAKISTAN);
        mLocales.put(UZBEK_AFGHANISTAN, UZBEK_AFGHANISTAN);
        mLocales.put(UZBEK_UZBEKISTAN, UZBEK_UZBEKISTAN);
        mLocales.put(VENDA, VENDA);
        mLocales.put(VIETNAMESE, VIETNAMESE);
        mLocales.put(VOLAPUK, VOLAPUK);
        mLocales.put(WALLOON, WALLOON);
        mLocales.put(WALAMO, WALAMO);
        mLocales.put(SORBIAN, SORBIAN);
        mLocales.put(WOLOF, WOLOF);
        mLocales.put(XHOSA, XHOSA);
        mLocales.put(YIDDISH, YIDDISH);
        mLocales.put(YORUBA, YORUBA);
        mLocales.put(ZHUANG, ZHUANG);
        mLocales.put(CHINESE_CHINA, CHINESE_CHINA);
        mLocales.put(CHINESE_HONG_KONG, CHINESE_HONG_KONG);
        mLocales.put(CHINESE_TRADITIONAL_SINGAPORE, CHINESE_TRADITIONAL_SINGAPORE);
        mLocales.put(CHINESE_TRADITIONAL_HONG_KONG, CHINESE_TRADITIONAL_HONG_KONG);
        mLocales.put(CHINESE_MACAO, CHINESE_MACAO);
        mLocales.put(CHINESE_SINGAPORE, CHINESE_SINGAPORE);
        mLocales.put(CHINESE_TAIWAN, CHINESE_TAIWAN);
        mLocales.put(ZULU, ZULU);
    }

    /**
     * Constant classes must have a private constructor.
     */
    private HaloLocale() {
    }

    @Nullable
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @HaloLocale.LocaleDefinition
    @SuppressWarnings("all")
    public static String fromLocale(@NonNull Locale locale) {
        AssertionUtils.notNull(locale, "locale");
        return mLocales.get(locale.toLanguageTag());
    }

    @Nullable
    @HaloLocale.LocaleDefinition
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String fromDefaultLocale() {
        return fromLocale(Locale.getDefault());
    }

}
