package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

/**
 * Provides all the market constants for segmentation
 */
@Keep
public final class HaloMarket {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({VANUATU,
            WALLIS_AND_FUTUNA,
            TURKS_AND_CAICOS_ISLANDS,
            UNITED_KINGDOM,
            UNITED_REPUBLIC_OF_TANZANIA,
            TONGA,
            SRI_LANKA,
            SWEDEN,
            SINGAPORE,
            SOMALIA,
            SAINT_PIERRE_AND_MIQUELON,
            SAUDI_ARABIA,
            QATAR,
            SAINT_BARTHÉLEMY,
            STATE_OF_PALESTINE,
            PHILIPPINES,
            NAURU,
            NICARAGUA,
            NORTHERN_MARIANA_ISLANDS,
            FEDERATED_STATES_OF_MICRONESIA,
            MONTSERRAT,
            LUXEMBOURG,
            MALAYSIA,
            MARTINIQUE,
            LESOTHO,
            KUWAIT,
            ITALY,
            KAZAKHSTAN,
            ISLAMIC_REPUBLIC_OF_IRAN,
            GUYANA,
            HONG_KONG,
            GUAM,
            GIBRALTAR,
            FINLAND,
            GABON,
            ESTONIA,
            ECUADOR,
            COTE_DIVOIRE,
            CZECH_REPUBLIC,
            COMOROS,
            CHILE,
            BULGARIA,
            CANADA,
            BERMUDA,
            BOTSWANA,
            BARBADOS,
            ANTIGUA_AND_BARBUDA,
            AUSTRIA,
            AMERICAN_SAMOA,
            US_VIRGIN_ISLANDS,
            ZIMBABWE,
            UNITED_ARAB_EMIRATES,
            UZBEKISTAN,
            TURKMENISTAN,
            TAJIKISTAN,
            TOKELAU,
            SPAIN,
            SWAZILAND,
            SOLOMON_ISLANDS,
            SAO_TOME_AND_PRINCIPE,
            SIERRA_LEONE,
            RWANDA,
            SAINT_MARTIN_FRENCH_PART,
            PUERTO_RICO,
            PERU,
            NORFOLK_ISLAND,
            PALAU,
            NEW_ZEALAND,
            NAMIBIA,
            MARSHALL_ISLANDS,
            MEXICO,
            MONTENEGRO,
            LITHUANIA,
            MALAWI,
            REPUBLIC_OF_KOREA,
            LEBANON,
            INDONESIA,
            ISRAEL,
            JORDAN,
            GUINEA_BISSAU,
            HONDURAS,
            FRENCH_SOUTHERN_TERRITORIES,
            GHANA,
            GUADELOUPE,
            ERITREA,
            FIJI,
            COSTA_RICA,
            CYPRUS,
            DOMINICAN_REPUBLIC,
            CHAD,
            COLOMBIA,
            BOSNIA_AND_HERZEGOVINA,
            BRUNEI_DARUSSALAM,
            CAMEROON,
            BANGLADESH,
            BENIN,
            ANTARCTICA,
            AUSTRALIA,
            ALGERIA,
            ZAMBIA,
            UKRAINE,
            URUGUAY,
            BRITISH_VIRGIN_ISLANDS,
            TOGO,
            TURKEY,
            SOUTH_SUDAN,
            SVALBARD_AND_JAN_MAYEN,
            TAIWAN,
            SEYCHELLES,
            SLOVENIA,
            RUSSIAN_FEDERATION,
            SAINT_LUCIA,
            SAN_MARINO,
            PAKISTAN,
            PARAGUAY,
            PORTUGAL,
            NEW_CALEDONIA,
            NIUE,
            MAYOTTE,
            MONGOLIA,
            MYANMAR,
            MADAGASCAR,
            MALTA,
            DEMOCRATIC_PEOPLES_REPUBLIC_OF_KOREA,
            LATVIA,
            LIECHTENSTEIN,
            ISLE_OF_MAN,
            JERSEY,
            HOLY_SEE_VATICAN_CITY_STATE,
            INDIA,
            GUINEA,
            GERMANY,
            GRENADA,
            EQUATORIAL_GUINEA,
            FAROE_ISLANDS,
            FRENCH_POLYNESIA,
            DOMINICA,
            COOK_ISLANDS,
            CURAÇAO,
            CENTRAL_AFRICAN_REPUBLIC,
            COCOS_KEELING_ISLANDS,
            CAMBODIA,
            BELIZE,
            SINT_EUSTATIUS_AND_SABA_BONAIRE,
            BRITISH_INDIAN_OCEAN_TERRITORY,
            ARUBA,
            BAHRAIN,
            ALBANIA,
            ANGUILLA,
            UNITED_STATES_MINOR_OUTLYING_ISLANDS,
            VIET_NAM,
            YEMEN,
            TUNISIA,
            UGANDA,
            SURINAME,
            SYRIAN_ARAB_REPUBLIC,
            TIMOR_LESTE,
            SLOVAKIA,
            SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS,
            SAMOA,
            SAINT_KITTS_AND_NEVIS,
            SERBIA,
            POLAND,
            ROMANIA,
            NIGERIA,
            OMAN,
            PAPUA_NEW_GUINEA,
            MONACO,
            MOZAMBIQUE,
            NETHERLANDS,
            MALI,
            MAURITIUS,
            LAO_PEOPLES_DEMOCRATIC_REPUBLIC,
            LIBYA,
            MACEDONIA,
            JAPAN,
            KIRIBATI,
            HEARD_ISLAND_AND_MCDONALD_ISLANDS,
            ICELAND,
            IRELAND,
            GREENLAND,
            GUERNSEY,
            FRENCH_GUIANA,
            GEORGIA,
            DJIBOUTI,
            EL_SALVADOR,
            FALKLAND_ISLANDS_MALVINAS,
            THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO,
            CUBA,
            CAYMAN_ISLANDS,
            CHRISTMAS_ISLAND,
            PLURINATIONAL_STATE_OF_BOLIVIA,
            BRAZIL,
            BURUNDI,
            BAHAMAS,
            BELGIUM,
            ANGOLA,
            ARMENIA,
            ÅLAND_ISLANDS,
            UNITED_STATES,
            BOLIVARIAN_REPUBLIC_OF_VENEZUELA,
            WESTERN_SAHARA,
            THAILAND,
            TRINIDAD_AND_TOBAGO,
            TUVALU,
            SUDAN,
            SWITZERLAND,
            SENEGAL,
            SINT_MAARTEN_DUTCH_PART,
            SOUTH_AFRICA,
            ASCENSION_AND_TRISTAN_DA_CUNHA_SAINT_HELENA,
            SAINT_VINCENT_AND_THE_GRENADINES,
            PANAMA,
            PITCAIRN,
            RÉUNION,
            NIGER,
            NORWAY,
            REPUBLIC_OF_MOLDOVA,
            MOROCCO,
            NEPAL,
            MALDIVES,
            MAURITANIA,
            KYRGYZSTAN,
            LIBERIA,
            MACAO,
            JAMAICA,
            KENYA,
            HUNGARY,
            IRAQ,
            GREECE,
            GUATEMALA,
            HAITI,
            FRANCE,
            GAMBIA,
            DENMARK,
            EGYPT,
            ETHIOPIA,
            CHINA,
            CONGO,
            CROATIA,
            BURKINA_FASO,
            CAPE_VERDE,
            BELARUS,
            BHUTAN,
            BOUVET_ISLAND,
            ARGENTINA,
            AZERBAIJAN,
            ANDORRA,
            AFGHANISTAN
    })
    /**
     * Market definition annotation to ensure the value passed is valid.
     */
    @Keep
    public @interface MarketDefinition {

    }

    /**
     * Vanuatu
     */
    public static final String VANUATU = "Vanuatu";
    /**
     * Wallis and Futuna
     */
    public static final String WALLIS_AND_FUTUNA = "Wallis and Futuna";
    /**
     * Turks and Caicos Islands
     */
    public static final String TURKS_AND_CAICOS_ISLANDS = "Turks and Caicos Islands";
    /**
     * United Kingdom
     */
    public static final String UNITED_KINGDOM = "United Kingdom";
    /**
     * United Republic of Tanzania
     */
    public static final String UNITED_REPUBLIC_OF_TANZANIA = "United Republic of Tanzania";
    /**
     * Tonga
     */
    public static final String TONGA = "Tonga";
    /**
     * Sri Lanka
     */
    public static final String SRI_LANKA = "Sri Lanka";
    /**
     * Sweden
     */
    public static final String SWEDEN = "Sweden";
    /**
     * Singapore
     */
    public static final String SINGAPORE = "Singapore";
    /**
     * Somalia
     */
    public static final String SOMALIA = "Somalia";
    /**
     * Saint Pierre and Miquelon
     */
    public static final String SAINT_PIERRE_AND_MIQUELON = "Saint Pierre and Miquelon";
    /**
     * Saudi Arabia
     */
    public static final String SAUDI_ARABIA = "Saudi Arabia";
    /**
     * Qatar
     */
    public static final String QATAR = "Qatar";
    /**
     * Saint Barthélemy
     */
    public static final String SAINT_BARTHÉLEMY = "Saint Barthélemy";
    /**
     * State of Palestine
     */
    public static final String STATE_OF_PALESTINE = "State of Palestine";
    /**
     * Philippines
     */
    public static final String PHILIPPINES = "Philippines";
    /**
     * Nauru
     */
    public static final String NAURU = "Nauru";
    /**
     * Nicaragua
     */
    public static final String NICARAGUA = "Nicaragua";
    /**
     * Northern Mariana Islands
     */
    public static final String NORTHERN_MARIANA_ISLANDS = "Northern Mariana Islands";
    /**
     * Federated States of Micronesia
     */
    public static final String FEDERATED_STATES_OF_MICRONESIA = "Federated States of Micronesia";
    /**
     * Montserrat
     */
    public static final String MONTSERRAT = "Montserrat";
    /**
     * Luxembourg
     */
    public static final String LUXEMBOURG = "Luxembourg";
    /**
     * Malaysia
     */
    public static final String MALAYSIA = "Malaysia";
    /**
     * Martinique
     */
    public static final String MARTINIQUE = "Martinique";
    /**
     * Lesotho
     */
    public static final String LESOTHO = "Lesotho";
    /**
     * Kuwait
     */
    public static final String KUWAIT = "Kuwait";
    /**
     * Italy
     */
    public static final String ITALY = "Italy";
    /**
     * Kazakhstan
     */
    public static final String KAZAKHSTAN = "Kazakhstan";
    /**
     * Islamic Republic of Iran
     */
    public static final String ISLAMIC_REPUBLIC_OF_IRAN = "Islamic Republic of Iran";
    /**
     * Guyana
     */
    public static final String GUYANA = "Guyana";
    /**
     * Hong Kong
     */
    public static final String HONG_KONG = "Hong Kong";
    /**
     * Guam
     */
    public static final String GUAM = "Guam";
    /**
     * Gibraltar
     */
    public static final String GIBRALTAR = "Gibraltar";
    /**
     * Finland
     */
    public static final String FINLAND = "Finland";
    /**
     * Gabon
     */
    public static final String GABON = "Gabon";
    /**
     * Estonia
     */
    public static final String ESTONIA = "Estonia";
    /**
     * Ecuador
     */
    public static final String ECUADOR = "Ecuador";
    /**
     * Côte d'Ivoire
     */
    public static final String COTE_DIVOIRE = "Côte d'Ivoire";
    /**
     * Czech Republic
     */
    public static final String CZECH_REPUBLIC = "Czech Republic";
    /**
     * Comoros
     */
    public static final String COMOROS = "Comoros";
    /**
     * Chile
     */
    public static final String CHILE = "Chile";
    /**
     * Bulgaria
     */
    public static final String BULGARIA = "Bulgaria";
    /**
     * Canada
     */
    public static final String CANADA = "Canada";
    /**
     * Bermuda
     */
    public static final String BERMUDA = "Bermuda";
    /**
     * Botswana
     */
    public static final String BOTSWANA = "Botswana";
    /**
     * Barbados
     */
    public static final String BARBADOS = "Barbados";
    /**
     * Antigua and Barbuda
     */
    public static final String ANTIGUA_AND_BARBUDA = "Antigua and Barbuda";
    /**
     * Austria
     */
    public static final String AUSTRIA = "Austria";
    /**
     * American Samoa
     */
    public static final String AMERICAN_SAMOA = "American Samoa";
    /**
     * U.S. Virgin Islands
     */
    public static final String US_VIRGIN_ISLANDS = "U.S. Virgin Islands";
    /**
     * Zimbabwe
     */
    public static final String ZIMBABWE = "Zimbabwe";
    /**
     * United Arab Emirates
     */
    public static final String UNITED_ARAB_EMIRATES = "United Arab Emirates";
    /**
     * Uzbekistan
     */
    public static final String UZBEKISTAN = "Uzbekistan";
    /**
     * Turkmenistan
     */
    public static final String TURKMENISTAN = "Turkmenistan";
    /**
     * Tajikistan
     */
    public static final String TAJIKISTAN = "Tajikistan";
    /**
     * Tokelau
     */
    public static final String TOKELAU = "Tokelau";
    /**
     * Spain
     */
    public static final String SPAIN = "Spain";
    /**
     * Swaziland
     */
    public static final String SWAZILAND = "Swaziland";
    /**
     * Solomon Islands
     */
    public static final String SOLOMON_ISLANDS = "Solomon Islands";
    /**
     * Sao Tome and Principe
     */
    public static final String SAO_TOME_AND_PRINCIPE = "Sao Tome and Principe";
    /**
     * Sierra Leone
     */
    public static final String SIERRA_LEONE = "Sierra Leone";
    /**
     * Rwanda
     */
    public static final String RWANDA = "Rwanda";
    /**
     * Saint Martin (French part)
     */
    public static final String SAINT_MARTIN_FRENCH_PART = "Saint Martin (French part)";
    /**
     * Puerto Rico
     */
    public static final String PUERTO_RICO = "Puerto Rico";
    /**
     * Peru
     */
    public static final String PERU = "Peru";
    /**
     * Norfolk Island
     */
    public static final String NORFOLK_ISLAND = "Norfolk Island";
    /**
     * Palau
     */
    public static final String PALAU = "Palau";
    /**
     * New Zealand
     */
    public static final String NEW_ZEALAND = "New Zealand";
    /**
     * Namibia
     */
    public static final String NAMIBIA = "Namibia";
    /**
     * Marshall Islands
     */
    public static final String MARSHALL_ISLANDS = "Marshall Islands";
    /**
     * Mexico
     */
    public static final String MEXICO = "Mexico";
    /**
     * Montenegro
     */
    public static final String MONTENEGRO = "Montenegro";
    /**
     * Lithuania
     */
    public static final String LITHUANIA = "Lithuania";
    /**
     * Malawi
     */
    public static final String MALAWI = "Malawi";
    /**
     * Republic of Korea
     */
    public static final String REPUBLIC_OF_KOREA = "Republic of Korea";
    /**
     * Lebanon
     */
    public static final String LEBANON = "Lebanon";
    /**
     * Indonesia
     */
    public static final String INDONESIA = "Indonesia";
    /**
     * Israel
     */
    public static final String ISRAEL = "Israel";
    /**
     * Jordan
     */
    public static final String JORDAN = "Jordan";
    /**
     * Guinea-Bissau
     */
    public static final String GUINEA_BISSAU = "Guinea-Bissau";
    /**
     * Honduras
     */
    public static final String HONDURAS = "Honduras";
    /**
     * French Southern Territories
     */
    public static final String FRENCH_SOUTHERN_TERRITORIES = "French Southern Territories";
    /**
     * Ghana
     */
    public static final String GHANA = "Ghana";
    /**
     * Guadeloupe
     */
    public static final String GUADELOUPE = "Guadeloupe";
    /**
     * Eritrea
     */
    public static final String ERITREA = "Eritrea";
    /**
     * Fiji
     */
    public static final String FIJI = "Fiji";
    /**
     * Costa Rica
     */
    public static final String COSTA_RICA = "Costa Rica";
    /**
     * Cyprus
     */
    public static final String CYPRUS = "Cyprus";
    /**
     * Dominican Republic
     */
    public static final String DOMINICAN_REPUBLIC = "Dominican Republic";
    /**
     * Chad
     */
    public static final String CHAD = "Chad";
    /**
     * Colombia
     */
    public static final String COLOMBIA = "Colombia";
    /**
     * Bosnia and Herzegovina
     */
    public static final String BOSNIA_AND_HERZEGOVINA = "Bosnia and Herzegovina";
    /**
     * Brunei Darussalam
     */
    public static final String BRUNEI_DARUSSALAM = "Brunei Darussalam";
    /**
     * Cameroon
     */
    public static final String CAMEROON = "Cameroon";
    /**
     * Bangladesh
     */
    public static final String BANGLADESH = "Bangladesh";
    /**
     * Benin
     */
    public static final String BENIN = "Benin";
    /**
     * Antarctica
     */
    public static final String ANTARCTICA = "Antarctica";
    /**
     * Australia
     */
    public static final String AUSTRALIA = "Australia";
    /**
     * Algeria
     */
    public static final String ALGERIA = "Algeria";
    /**
     * Zambia
     */
    public static final String ZAMBIA = "Zambia";
    /**
     * Ukraine
     */
    public static final String UKRAINE = "Ukraine";
    /**
     * Uruguay
     */
    public static final String URUGUAY = "Uruguay";
    /**
     * British Virgin Islands
     */
    public static final String BRITISH_VIRGIN_ISLANDS = "British Virgin Islands";
    /**
     * Togo
     */
    public static final String TOGO = "Togo";
    /**
     * Turkey
     */
    public static final String TURKEY = "Turkey";
    /**
     * South Sudan
     */
    public static final String SOUTH_SUDAN = "South Sudan";
    /**
     * Svalbard and Jan Mayen
     */
    public static final String SVALBARD_AND_JAN_MAYEN = "Svalbard and Jan Mayen";
    /**
     * Taiwan
     */
    public static final String TAIWAN = "Taiwan";
    /**
     * Seychelles
     */
    public static final String SEYCHELLES = "Seychelles";
    /**
     * Slovenia
     */
    public static final String SLOVENIA = "Slovenia";
    /**
     * Russian Federation
     */
    public static final String RUSSIAN_FEDERATION = "Russian Federation";
    /**
     * Saint Lucia
     */
    public static final String SAINT_LUCIA = "Saint Lucia";
    /**
     * San Marino
     */
    public static final String SAN_MARINO = "San Marino";
    /**
     * Pakistan
     */
    public static final String PAKISTAN = "Pakistan";
    /**
     * Paraguay
     */
    public static final String PARAGUAY = "Paraguay";
    /**
     * Portugal
     */
    public static final String PORTUGAL = "Portugal";
    /**
     * New Caledonia
     */
    public static final String NEW_CALEDONIA = "New Caledonia";
    /**
     * Niue
     */
    public static final String NIUE = "Niue";
    /**
     * Mayotte
     */
    public static final String MAYOTTE = "Mayotte";
    /**
     * Mongolia
     */
    public static final String MONGOLIA = "Mongolia";
    /**
     * Myanmar
     */
    public static final String MYANMAR = "Myanmar";
    /**
     * Madagascar
     */
    public static final String MADAGASCAR = "Madagascar";
    /**
     * Malta
     */
    public static final String MALTA = "Malta";
    /**
     * Democratic People's Republic of Korea
     */
    public static final String DEMOCRATIC_PEOPLES_REPUBLIC_OF_KOREA = "Democratic People's Republic of Korea";
    /**
     * Latvia
     */
    public static final String LATVIA = "Latvia";
    /**
     * Liechtenstein
     */
    public static final String LIECHTENSTEIN = "Liechtenstein";
    /**
     * Isle of Man
     */
    public static final String ISLE_OF_MAN = "Isle of Man";
    /**
     * Jersey
     */
    public static final String JERSEY = "Jersey";
    /**
     * Holy See (Vatican City State)
     */
    public static final String HOLY_SEE_VATICAN_CITY_STATE = "Holy See (Vatican City State)";
    /**
     * India
     */
    public static final String INDIA = "India";
    /**
     * Guinea
     */
    public static final String GUINEA = "Guinea";
    /**
     * Germany
     */
    public static final String GERMANY = "Germany";
    /**
     * Grenada
     */
    public static final String GRENADA = "Grenada";
    /**
     * Equatorial Guinea
     */
    public static final String EQUATORIAL_GUINEA = "Equatorial Guinea";
    /**
     * Faroe Islands
     */
    public static final String FAROE_ISLANDS = "Faroe Islands";
    /**
     * French Polynesia
     */
    public static final String FRENCH_POLYNESIA = "French Polynesia";
    /**
     * Dominica
     */
    public static final String DOMINICA = "Dominica";
    /**
     * Cook Islands
     */
    public static final String COOK_ISLANDS = "Cook Islands";
    /**
     * Curaçao
     */
    public static final String CURAÇAO = "Curaçao";
    /**
     * Central African Republic
     */
    public static final String CENTRAL_AFRICAN_REPUBLIC = "Central African Republic";
    /**
     * Cocos (Keeling) Islands
     */
    public static final String COCOS_KEELING_ISLANDS = "Cocos (Keeling) Islands";
    /**
     * Cambodia
     */
    public static final String CAMBODIA = "Cambodia";
    /**
     * Belize
     */
    public static final String BELIZE = "Belize";
    /**
     * Sint Eustatius and Saba Bonaire
     */
    public static final String SINT_EUSTATIUS_AND_SABA_BONAIRE = "Sint Eustatius and Saba Bonaire";
    /**
     * British Indian Ocean Territory
     */
    public static final String BRITISH_INDIAN_OCEAN_TERRITORY = "British Indian Ocean Territory";
    /**
     * Aruba
     */
    public static final String ARUBA = "Aruba";
    /**
     * Bahrain
     */
    public static final String BAHRAIN = "Bahrain";
    /**
     * Albania
     */
    public static final String ALBANIA = "Albania";
    /**
     * Anguilla
     */
    public static final String ANGUILLA = "Anguilla";
    /**
     * United States Minor Outlying Islands
     */
    public static final String UNITED_STATES_MINOR_OUTLYING_ISLANDS = "United States Minor Outlying Islands";
    /**
     * Viet Nam
     */
    public static final String VIET_NAM = "Viet Nam";
    /**
     * Yemen
     */
    public static final String YEMEN = "Yemen";
    /**
     * Tunisia
     */
    public static final String TUNISIA = "Tunisia";
    /**
     * Uganda
     */
    public static final String UGANDA = "Uganda";
    /**
     * Suriname
     */
    public static final String SURINAME = "Suriname";
    /**
     * Syrian Arab Republic
     */
    public static final String SYRIAN_ARAB_REPUBLIC = "Syrian Arab Republic";
    /**
     * Timor-Leste
     */
    public static final String TIMOR_LESTE = "Timor-Leste";
    /**
     * Slovakia
     */
    public static final String SLOVAKIA = "Slovakia";
    /**
     * South Georgia and the South Sandwich Islands
     */
    public static final String SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS = "South Georgia and the South Sandwich Islands";
    /**
     * Samoa
     */
    public static final String SAMOA = "Samoa";
    /**
     * Saint Kitts and Nevis
     */
    public static final String SAINT_KITTS_AND_NEVIS = "Saint Kitts and Nevis";
    /**
     * Serbia
     */
    public static final String SERBIA = "Serbia";
    /**
     * Poland
     */
    public static final String POLAND = "Poland";
    /**
     * Romania
     */
    public static final String ROMANIA = "Romania";
    /**
     * Nigeria
     */
    public static final String NIGERIA = "Nigeria";
    /**
     * Oman
     */
    public static final String OMAN = "Oman";
    /**
     * Papua New Guinea
     */
    public static final String PAPUA_NEW_GUINEA = "Papua New Guinea";
    /**
     * Monaco
     */
    public static final String MONACO = "Monaco";
    /**
     * Mozambique
     */
    public static final String MOZAMBIQUE = "Mozambique";
    /**
     * Netherlands
     */
    public static final String NETHERLANDS = "Netherlands";
    /**
     * Mali
     */
    public static final String MALI = "Mali";
    /**
     * Mauritius
     */
    public static final String MAURITIUS = "Mauritius";
    /**
     * Lao People's Democratic Republic
     */
    public static final String LAO_PEOPLES_DEMOCRATIC_REPUBLIC = "Lao People's Democratic Republic";
    /**
     * Libya
     */
    public static final String LIBYA = "Libya";
    /**
     * Macedonia
     */
    public static final String MACEDONIA = "Macedonia";
    /**
     * Japan
     */
    public static final String JAPAN = "Japan";
    /**
     * Kiribati
     */
    public static final String KIRIBATI = "Kiribati";
    /**
     * Heard Island and McDonald Islands
     */
    public static final String HEARD_ISLAND_AND_MCDONALD_ISLANDS = "Heard Island and McDonald Islands";
    /**
     * Iceland
     */
    public static final String ICELAND = "Iceland";
    /**
     * Ireland
     */
    public static final String IRELAND = "Ireland";
    /**
     * Greenland
     */
    public static final String GREENLAND = "Greenland";
    /**
     * Guernsey
     */
    public static final String GUERNSEY = "Guernsey";
    /**
     * French Guiana
     */
    public static final String FRENCH_GUIANA = "French Guiana";
    /**
     * Georgia
     */
    public static final String GEORGIA = "Georgia";
    /**
     * Djibouti
     */
    public static final String DJIBOUTI = "Djibouti";
    /**
     * El Salvador
     */
    public static final String EL_SALVADOR = "El Salvador";
    /**
     * Falkland Islands (Malvinas)
     */
    public static final String FALKLAND_ISLANDS_MALVINAS = "Falkland Islands (Malvinas)";
    /**
     * The Democratic Republic of the Congo
     */
    public static final String THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO = "The Democratic Republic of the Congo";
    /**
     * Cuba
     */
    public static final String CUBA = "Cuba";
    /**
     * Cayman Islands
     */
    public static final String CAYMAN_ISLANDS = "Cayman Islands";
    /**
     * Christmas Island
     */
    public static final String CHRISTMAS_ISLAND = "Christmas Island";
    /**
     * Plurinational State of Bolivia
     */
    public static final String PLURINATIONAL_STATE_OF_BOLIVIA = "Plurinational State of Bolivia";
    /**
     * Brazil
     */
    public static final String BRAZIL = "Brazil";
    /**
     * Burundi
     */
    public static final String BURUNDI = "Burundi";
    /**
     * Bahamas
     */
    public static final String BAHAMAS = "Bahamas";
    /**
     * Belgium
     */
    public static final String BELGIUM = "Belgium";
    /**
     * Angola
     */
    public static final String ANGOLA = "Angola";
    /**
     * Armenia
     */
    public static final String ARMENIA = "Armenia";
    /**
     * Åland Islands
     */
    public static final String ÅLAND_ISLANDS = "Åland Islands";
    /**
     * United States
     */
    public static final String UNITED_STATES = "United States";
    /**
     * Bolivarian Republic of Venezuela
     */
    public static final String BOLIVARIAN_REPUBLIC_OF_VENEZUELA = "Bolivarian Republic of Venezuela";
    /**
     * Western Sahara
     */
    public static final String WESTERN_SAHARA = "Western Sahara";
    /**
     * Thailand
     */
    public static final String THAILAND = "Thailand";
    /**
     * Trinidad and Tobago
     */
    public static final String TRINIDAD_AND_TOBAGO = "Trinidad and Tobago";
    /**
     * Tuvalu
     */
    public static final String TUVALU = "Tuvalu";
    /**
     * Sudan
     */
    public static final String SUDAN = "Sudan";
    /**
     * Switzerland
     */
    public static final String SWITZERLAND = "Switzerland";
    /**
     * Senegal
     */
    public static final String SENEGAL = "Senegal";
    /**
     * Sint Maarten (Dutch part)
     */
    public static final String SINT_MAARTEN_DUTCH_PART = "Sint Maarten (Dutch part)";
    /**
     * South Africa
     */
    public static final String SOUTH_AFRICA = "South Africa";
    /**
     * Ascension and Tristan da Cunha Saint Helena
     */
    public static final String ASCENSION_AND_TRISTAN_DA_CUNHA_SAINT_HELENA = "Ascension and Tristan da Cunha Saint Helena";
    /**
     * Saint Vincent and the Grenadines
     */
    public static final String SAINT_VINCENT_AND_THE_GRENADINES = "Saint Vincent and the Grenadines";
    /**
     * Panama
     */
    public static final String PANAMA = "Panama";
    /**
     * Pitcairn
     */
    public static final String PITCAIRN = "Pitcairn";
    /**
     * Réunion
     */
    public static final String RÉUNION = "Réunion";
    /**
     * Niger
     */
    public static final String NIGER = "Niger";
    /**
     * Norway
     */
    public static final String NORWAY = "Norway";
    /**
     * Republic of Moldova
     */
    public static final String REPUBLIC_OF_MOLDOVA = "Republic of Moldova";
    /**
     * Morocco
     */
    public static final String MOROCCO = "Morocco";
    /**
     * Nepal
     */
    public static final String NEPAL = "Nepal";
    /**
     * Maldives
     */
    public static final String MALDIVES = "Maldives";
    /**
     * Mauritania
     */
    public static final String MAURITANIA = "Mauritania";
    /**
     * Kyrgyzstan
     */
    public static final String KYRGYZSTAN = "Kyrgyzstan";
    /**
     * Liberia
     */
    public static final String LIBERIA = "Liberia";
    /**
     * Macao
     */
    public static final String MACAO = "Macao";
    /**
     * Jamaica
     */
    public static final String JAMAICA = "Jamaica";
    /**
     * Kenya
     */
    public static final String KENYA = "Kenya";
    /**
     * Hungary
     */
    public static final String HUNGARY = "Hungary";
    /**
     * Iraq
     */
    public static final String IRAQ = "Iraq";
    /**
     * Greece
     */
    public static final String GREECE = "Greece";
    /**
     * Guatemala
     */
    public static final String GUATEMALA = "Guatemala";
    /**
     * Haiti
     */
    public static final String HAITI = "Haiti";
    /**
     * France
     */
    public static final String FRANCE = "France";
    /**
     * Gambia
     */
    public static final String GAMBIA = "Gambia";
    /**
     * Denmark
     */
    public static final String DENMARK = "Denmark";
    /**
     * Egypt
     */
    public static final String EGYPT = "Egypt";
    /**
     * Ethiopia
     */
    public static final String ETHIOPIA = "Ethiopia";
    /**
     * China
     */
    public static final String CHINA = "China";
    /**
     * Congo
     */
    public static final String CONGO = "Congo";
    /**
     * Croatia
     */
    public static final String CROATIA = "Croatia";
    /**
     * Burkina Faso
     */
    public static final String BURKINA_FASO = "Burkina Faso";
    /**
     * Cape Verde
     */
    public static final String CAPE_VERDE = "Cape Verde";
    /**
     * Belarus
     */
    public static final String BELARUS = "Belarus";
    /**
     * Bhutan
     */
    public static final String BHUTAN = "Bhutan";
    /**
     * Bouvet Island
     */
    public static final String BOUVET_ISLAND = "Bouvet Island";
    /**
     * Argentina
     */
    public static final String ARGENTINA = "Argentina";
    /**
     * Azerbaijan
     */
    public static final String AZERBAIJAN = "Azerbaijan";
    /**
     * Andorra
     */
    public static final String ANDORRA = "Andorra";
    /**
     * Afghanistan
     */
    public static final String AFGHANISTAN = "Afghanistan";


    /**
     * Market id references.
     */
    private static HashMap<String, String> mMarkets = new HashMap<>();

    static {
        mMarkets.put(VANUATU, "000000000000000000000339");
        mMarkets.put(WALLIS_AND_FUTUNA, "000000000000000000000344");
        mMarkets.put(TURKS_AND_CAICOS_ISLANDS, "000000000000000000000329");
        mMarkets.put(UNITED_KINGDOM, "000000000000000000000334");
        mMarkets.put(UNITED_REPUBLIC_OF_TANZANIA, "000000000000000000000319");
        mMarkets.put(TONGA, "000000000000000000000324");
        mMarkets.put(SRI_LANKA, "000000000000000000000309");
        mMarkets.put(SWEDEN, "000000000000000000000314");
        mMarkets.put(SINGAPORE, "000000000000000000000299");
        mMarkets.put(SOMALIA, "000000000000000000000304");
        mMarkets.put(SAINT_PIERRE_AND_MIQUELON, "000000000000000000000289");
        mMarkets.put(SAUDI_ARABIA, "000000000000000000000294");
        mMarkets.put(QATAR, "000000000000000000000279");
        mMarkets.put(SAINT_BARTHÉLEMY, "000000000000000000000284");
        mMarkets.put(STATE_OF_PALESTINE, "000000000000000000000269");
        mMarkets.put(PHILIPPINES, "000000000000000000000274");
        mMarkets.put(NAURU, "000000000000000000000254");
        mMarkets.put(NICARAGUA, "000000000000000000000259");
        mMarkets.put(NORTHERN_MARIANA_ISLANDS, "000000000000000000000264");
        mMarkets.put(FEDERATED_STATES_OF_MICRONESIA, "000000000000000000000244");
        mMarkets.put(MONTSERRAT, "000000000000000000000249");
        mMarkets.put(LUXEMBOURG, "000000000000000000000229");
        mMarkets.put(MALAYSIA, "000000000000000000000234");
        mMarkets.put(MARTINIQUE, "000000000000000000000239");
        mMarkets.put(LESOTHO, "000000000000000000000224");
        mMarkets.put(KUWAIT, "000000000000000000000219");
        mMarkets.put(ITALY, "000000000000000000000209");
        mMarkets.put(KAZAKHSTAN, "000000000000000000000214");
        mMarkets.put(ISLAMIC_REPUBLIC_OF_IRAN, "000000000000000000000204");
        mMarkets.put(GUYANA, "000000000000000000000194");
        mMarkets.put(HONG_KONG, "000000000000000000000199");
        mMarkets.put(GUAM, "000000000000000000000189");
        mMarkets.put(GIBRALTAR, "000000000000000000000184");
        mMarkets.put(FINLAND, "000000000000000000000174");
        mMarkets.put(GABON, "000000000000000000000179");
        mMarkets.put(ESTONIA, "000000000000000000000169");
        mMarkets.put(ECUADOR, "000000000000000000000164");
        mMarkets.put(COTE_DIVOIRE, "000000000000000000000154");
        mMarkets.put(CZECH_REPUBLIC, "000000000000000000000159");
        mMarkets.put(COMOROS, "000000000000000000000149");
        mMarkets.put(CHILE, "000000000000000000000144");
        mMarkets.put(BULGARIA, "000000000000000000000134");
        mMarkets.put(CANADA, "000000000000000000000139");
        mMarkets.put(BERMUDA, "000000000000000000000124");
        mMarkets.put(BOTSWANA, "000000000000000000000129");
        mMarkets.put(BARBADOS, "000000000000000000000119");
        mMarkets.put(ANTIGUA_AND_BARBUDA, "000000000000000000000109");
        mMarkets.put(AUSTRIA, "000000000000000000000114");
        mMarkets.put(AMERICAN_SAMOA, "000000000000000000000104");
        mMarkets.put(US_VIRGIN_ISLANDS, "000000000000000000000343");
        mMarkets.put(ZIMBABWE, "000000000000000000000348");
        mMarkets.put(UNITED_ARAB_EMIRATES, "000000000000000000000333");
        mMarkets.put(UZBEKISTAN, "000000000000000000000338");
        mMarkets.put(TURKMENISTAN, "000000000000000000000328");
        mMarkets.put(TAJIKISTAN, "000000000000000000000318");
        mMarkets.put(TOKELAU, "000000000000000000000323");
        mMarkets.put(SPAIN, "000000000000000000000308");
        mMarkets.put(SWAZILAND, "000000000000000000000313");
        mMarkets.put(SOLOMON_ISLANDS, "000000000000000000000303");
        mMarkets.put(SAO_TOME_AND_PRINCIPE, "000000000000000000000293");
        mMarkets.put(SIERRA_LEONE, "000000000000000000000298");
        mMarkets.put(RWANDA, "000000000000000000000283");
        mMarkets.put(SAINT_MARTIN_FRENCH_PART, "000000000000000000000288");
        mMarkets.put(PUERTO_RICO, "000000000000000000000278");
        mMarkets.put(PERU, "000000000000000000000273");
        mMarkets.put(NORFOLK_ISLAND, "000000000000000000000263");
        mMarkets.put(PALAU, "000000000000000000000268");
        mMarkets.put(NEW_ZEALAND, "000000000000000000000258");
        mMarkets.put(NAMIBIA, "000000000000000000000253");
        mMarkets.put(MARSHALL_ISLANDS, "000000000000000000000238");
        mMarkets.put(MEXICO, "000000000000000000000243");
        mMarkets.put(MONTENEGRO, "000000000000000000000248");
        mMarkets.put(LITHUANIA, "000000000000000000000228");
        mMarkets.put(MALAWI, "000000000000000000000233");
        mMarkets.put(REPUBLIC_OF_KOREA, "000000000000000000000218");
        mMarkets.put(LEBANON, "000000000000000000000223");
        mMarkets.put(INDONESIA, "000000000000000000000203");
        mMarkets.put(ISRAEL, "000000000000000000000208");
        mMarkets.put(JORDAN, "000000000000000000000213");
        mMarkets.put(GUINEA_BISSAU, "000000000000000000000193");
        mMarkets.put(HONDURAS, "000000000000000000000198");
        mMarkets.put(FRENCH_SOUTHERN_TERRITORIES, "000000000000000000000178");
        mMarkets.put(GHANA, "000000000000000000000183");
        mMarkets.put(GUADELOUPE, "000000000000000000000188");
        mMarkets.put(ERITREA, "000000000000000000000168");
        mMarkets.put(FIJI, "000000000000000000000173");
        mMarkets.put(COSTA_RICA, "000000000000000000000153");
        mMarkets.put(CYPRUS, "000000000000000000000158");
        mMarkets.put(DOMINICAN_REPUBLIC, "000000000000000000000163");
        mMarkets.put(CHAD, "000000000000000000000143");
        mMarkets.put(COLOMBIA, "000000000000000000000148");
        mMarkets.put(BOSNIA_AND_HERZEGOVINA, "000000000000000000000128");
        mMarkets.put(BRUNEI_DARUSSALAM, "000000000000000000000133");
        mMarkets.put(CAMEROON, "000000000000000000000138");
        mMarkets.put(BANGLADESH, "000000000000000000000118");
        mMarkets.put(BENIN, "000000000000000000000123");
        mMarkets.put(ANTARCTICA, "000000000000000000000108");
        mMarkets.put(AUSTRALIA, "000000000000000000000113");
        mMarkets.put(ALGERIA, "000000000000000000000103");
        mMarkets.put(ZAMBIA, "000000000000000000000347");
        mMarkets.put(UKRAINE, "000000000000000000000332");
        mMarkets.put(URUGUAY, "000000000000000000000337");
        mMarkets.put(BRITISH_VIRGIN_ISLANDS, "000000000000000000000342");
        mMarkets.put(TOGO, "000000000000000000000322");
        mMarkets.put(TURKEY, "000000000000000000000327");
        mMarkets.put(SOUTH_SUDAN, "000000000000000000000307");
        mMarkets.put(SVALBARD_AND_JAN_MAYEN, "000000000000000000000312");
        mMarkets.put(TAIWAN, "000000000000000000000317");
        mMarkets.put(SEYCHELLES, "000000000000000000000297");
        mMarkets.put(SLOVENIA, "000000000000000000000302");
        mMarkets.put(RUSSIAN_FEDERATION, "000000000000000000000282");
        mMarkets.put(SAINT_LUCIA, "000000000000000000000287");
        mMarkets.put(SAN_MARINO, "000000000000000000000292");
        mMarkets.put(PAKISTAN, "000000000000000000000267");
        mMarkets.put(PARAGUAY, "000000000000000000000272");
        mMarkets.put(PORTUGAL, "000000000000000000000277");
        mMarkets.put(NEW_CALEDONIA, "000000000000000000000257");
        mMarkets.put(NIUE, "000000000000000000000262");
        mMarkets.put(MAYOTTE, "000000000000000000000242");
        mMarkets.put(MONGOLIA, "000000000000000000000247");
        mMarkets.put(MYANMAR, "000000000000000000000252");
        mMarkets.put(MADAGASCAR, "000000000000000000000232");
        mMarkets.put(MALTA, "000000000000000000000237");
        mMarkets.put(DEMOCRATIC_PEOPLES_REPUBLIC_OF_KOREA, "000000000000000000000217");
        mMarkets.put(LATVIA, "000000000000000000000222");
        mMarkets.put(LIECHTENSTEIN, "000000000000000000000227");
        mMarkets.put(ISLE_OF_MAN, "000000000000000000000207");
        mMarkets.put(JERSEY, "000000000000000000000212");
        mMarkets.put(HOLY_SEE_VATICAN_CITY_STATE, "000000000000000000000197");
        mMarkets.put(INDIA, "000000000000000000000202");
        mMarkets.put(GUINEA, "000000000000000000000192");
        mMarkets.put(GERMANY, "000000000000000000000182");
        mMarkets.put(GRENADA, "000000000000000000000187");
        mMarkets.put(EQUATORIAL_GUINEA, "000000000000000000000167");
        mMarkets.put(FAROE_ISLANDS, "000000000000000000000172");
        mMarkets.put(FRENCH_POLYNESIA, "000000000000000000000177");
        mMarkets.put(DOMINICA, "000000000000000000000162");
        mMarkets.put(COOK_ISLANDS, "000000000000000000000152");
        mMarkets.put(CURAÇAO, "000000000000000000000157");
        mMarkets.put(CENTRAL_AFRICAN_REPUBLIC, "000000000000000000000142");
        mMarkets.put(COCOS_KEELING_ISLANDS, "000000000000000000000147");
        mMarkets.put(CAMBODIA, "000000000000000000000137");
        mMarkets.put(BELIZE, "000000000000000000000122");
        mMarkets.put(SINT_EUSTATIUS_AND_SABA_BONAIRE, "000000000000000000000127");
        mMarkets.put(BRITISH_INDIAN_OCEAN_TERRITORY, "000000000000000000000132");
        mMarkets.put(ARUBA, "000000000000000000000112");
        mMarkets.put(BAHRAIN, "000000000000000000000117");
        mMarkets.put(ALBANIA, "000000000000000000000102");
        mMarkets.put(ANGUILLA, "000000000000000000000107");
        mMarkets.put(UNITED_STATES_MINOR_OUTLYING_ISLANDS, "000000000000000000000336");
        mMarkets.put(VIET_NAM, "000000000000000000000341");
        mMarkets.put(YEMEN, "000000000000000000000346");
        mMarkets.put(TUNISIA, "000000000000000000000326");
        mMarkets.put(UGANDA, "000000000000000000000331");
        mMarkets.put(SURINAME, "000000000000000000000311");
        mMarkets.put(SYRIAN_ARAB_REPUBLIC, "000000000000000000000316");
        mMarkets.put(TIMOR_LESTE, "000000000000000000000321");
        mMarkets.put(SLOVAKIA, "000000000000000000000301");
        mMarkets.put(SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS, "000000000000000000000306");
        mMarkets.put(SAMOA, "000000000000000000000291");
        mMarkets.put(SAINT_KITTS_AND_NEVIS, "000000000000000000000286");
        mMarkets.put(SERBIA, "000000000000000000000296");
        mMarkets.put(POLAND, "000000000000000000000276");
        mMarkets.put(ROMANIA, "000000000000000000000281");
        mMarkets.put(NIGERIA, "000000000000000000000261");
        mMarkets.put(OMAN, "000000000000000000000266");
        mMarkets.put(PAPUA_NEW_GUINEA, "000000000000000000000271");
        mMarkets.put(MONACO, "000000000000000000000246");
        mMarkets.put(MOZAMBIQUE, "000000000000000000000251");
        mMarkets.put(NETHERLANDS, "000000000000000000000256");
        mMarkets.put(MALI, "000000000000000000000236");
        mMarkets.put(MAURITIUS, "000000000000000000000241");
        mMarkets.put(LAO_PEOPLES_DEMOCRATIC_REPUBLIC, "000000000000000000000221");
        mMarkets.put(LIBYA, "000000000000000000000226");
        mMarkets.put(MACEDONIA, "000000000000000000000231");
        mMarkets.put(JAPAN, "000000000000000000000211");
        mMarkets.put(KIRIBATI, "000000000000000000000216");
        mMarkets.put(HEARD_ISLAND_AND_MCDONALD_ISLANDS, "000000000000000000000196");
        mMarkets.put(ICELAND, "000000000000000000000201");
        mMarkets.put(IRELAND, "000000000000000000000206");
        mMarkets.put(GREENLAND, "000000000000000000000186");
        mMarkets.put(GUERNSEY, "000000000000000000000191");
        mMarkets.put(FRENCH_GUIANA, "000000000000000000000176");
        mMarkets.put(GEORGIA, "000000000000000000000181");
        mMarkets.put(DJIBOUTI, "000000000000000000000161");
        mMarkets.put(EL_SALVADOR, "000000000000000000000166");
        mMarkets.put(FALKLAND_ISLANDS_MALVINAS, "000000000000000000000171");
        mMarkets.put(THE_DEMOCRATIC_REPUBLIC_OF_THE_CONGO, "000000000000000000000151");
        mMarkets.put(CUBA, "000000000000000000000156");
        mMarkets.put(CAYMAN_ISLANDS, "000000000000000000000141");
        mMarkets.put(CHRISTMAS_ISLAND, "000000000000000000000146");
        mMarkets.put(PLURINATIONAL_STATE_OF_BOLIVIA, "000000000000000000000126");
        mMarkets.put(BRAZIL, "000000000000000000000131");
        mMarkets.put(BURUNDI, "000000000000000000000136");
        mMarkets.put(BAHAMAS, "000000000000000000000116");
        mMarkets.put(BELGIUM, "000000000000000000000121");
        mMarkets.put(ANGOLA, "000000000000000000000106");
        mMarkets.put(ARMENIA, "000000000000000000000111");
        mMarkets.put(ÅLAND_ISLANDS, "000000000000000000000101");
        mMarkets.put(UNITED_STATES, "000000000000000000000335");
        mMarkets.put(BOLIVARIAN_REPUBLIC_OF_VENEZUELA, "000000000000000000000340");
        mMarkets.put(WESTERN_SAHARA, "000000000000000000000345");
        mMarkets.put(THAILAND, "000000000000000000000320");
        mMarkets.put(TRINIDAD_AND_TOBAGO, "000000000000000000000325");
        mMarkets.put(TUVALU, "000000000000000000000330");
        mMarkets.put(SUDAN, "000000000000000000000310");
        mMarkets.put(SWITZERLAND, "000000000000000000000315");
        mMarkets.put(SENEGAL, "000000000000000000000295");
        mMarkets.put(SINT_MAARTEN_DUTCH_PART, "000000000000000000000300");
        mMarkets.put(SOUTH_AFRICA, "000000000000000000000305");
        mMarkets.put(ASCENSION_AND_TRISTAN_DA_CUNHA_SAINT_HELENA, "000000000000000000000285");
        mMarkets.put(SAINT_VINCENT_AND_THE_GRENADINES, "000000000000000000000290");
        mMarkets.put(PANAMA, "000000000000000000000270");
        mMarkets.put(PITCAIRN, "000000000000000000000275");
        mMarkets.put(RÉUNION, "000000000000000000000280");
        mMarkets.put(NIGER, "000000000000000000000260");
        mMarkets.put(NORWAY, "000000000000000000000265");
        mMarkets.put(REPUBLIC_OF_MOLDOVA, "000000000000000000000245");
        mMarkets.put(MOROCCO, "000000000000000000000250");
        mMarkets.put(NEPAL, "000000000000000000000255");
        mMarkets.put(MALDIVES, "000000000000000000000235");
        mMarkets.put(MAURITANIA, "000000000000000000000240");
        mMarkets.put(KYRGYZSTAN, "000000000000000000000220");
        mMarkets.put(LIBERIA, "000000000000000000000225");
        mMarkets.put(MACAO, "000000000000000000000230");
        mMarkets.put(JAMAICA, "000000000000000000000210");
        mMarkets.put(KENYA, "000000000000000000000215");
        mMarkets.put(HUNGARY, "000000000000000000000200");
        mMarkets.put(IRAQ, "000000000000000000000205");
        mMarkets.put(GREECE, "000000000000000000000185");
        mMarkets.put(GUATEMALA, "000000000000000000000190");
        mMarkets.put(HAITI, "000000000000000000000195");
        mMarkets.put(FRANCE, "000000000000000000000175");
        mMarkets.put(GAMBIA, "000000000000000000000180");
        mMarkets.put(DENMARK, "000000000000000000000160");
        mMarkets.put(EGYPT, "000000000000000000000165");
        mMarkets.put(ETHIOPIA, "000000000000000000000170");
        mMarkets.put(CHINA, "000000000000000000000145");
        mMarkets.put(CONGO, "000000000000000000000150");
        mMarkets.put(CROATIA, "000000000000000000000155");
        mMarkets.put(BURKINA_FASO, "000000000000000000000135");
        mMarkets.put(CAPE_VERDE, "000000000000000000000140");
        mMarkets.put(BELARUS, "000000000000000000000120");
        mMarkets.put(BHUTAN, "000000000000000000000125");
        mMarkets.put(BOUVET_ISLAND, "000000000000000000000130");
        mMarkets.put(ARGENTINA, "000000000000000000000110");
        mMarkets.put(AZERBAIJAN, "000000000000000000000115");
        mMarkets.put(ANDORRA, "000000000000000000000105");
        mMarkets.put(AFGHANISTAN, "000000000000000000000100");
    }

    /**
     * Constant classes must have a private constructor.
     */
    private HaloMarket() {
    }


    /**
     * Returns the id of the market.
     *
     * @param market The market to create a segmentation tag
     * @return The id of the market.
     */
    @NonNull
    public static String idFromMarket(@MarketDefinition String market) {
        AssertionUtils.notNull(market, "market");
        return mMarkets.get(market);
    }
}
