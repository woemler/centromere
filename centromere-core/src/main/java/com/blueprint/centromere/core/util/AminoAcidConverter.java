/*
 * Copyright 2018 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blueprint.centromere.core.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woemler
 */
public class AminoAcidConverter {
  
  private static final Logger logger = LoggerFactory.getLogger(AminoAcidConverter.class);
  private static final Map<String, String> shortToSingleLetterMap;
  private static final Map<String, String> singleLetterToShortMap;
  private static final Pattern SHORT_PATTERN = Pattern.compile("[A-Z][a-z]{2}");
  private static final Pattern SINGLE_PATTERN = Pattern.compile("[A-Z]");
  
  static {
    Map<String,String> map = new HashMap<>();
    map.put("ala", "A");
    map.put("arg", "R");
    map.put("asn", "N");
    map.put("asp", "D");
    map.put("cys", "C");
    map.put("glu", "E");
    map.put("gln", "Q");
    map.put("gly", "G");
    map.put("his", "H");
    map.put("ile", "I");
    map.put("leu", "L");
    map.put("lys", "K");
    map.put("met", "M");
    map.put("phe", "F");
    map.put("pro", "P");
    map.put("ser", "S");
    map.put("thr", "T");
    map.put("trp", "W");
    map.put("tyr", "Y");
    map.put("val", "V");
    map.put("ter", "X");
    shortToSingleLetterMap = Collections.unmodifiableMap(map);
    map = new HashMap<>();
    map.put("A", "Ala");
    map.put("R", "Arg");
    map.put("N", "Asn");
    map.put("D", "Asp");
    map.put("C", "Cys");
    map.put("E", "Glu");
    map.put("Q", "Gln");
    map.put("G", "Gly");
    map.put("H", "His");
    map.put("I", "Ile");
    map.put("L", "Leu");
    map.put("K", "Lys");
    map.put("M", "Met");
    map.put("F", "Phe");
    map.put("P", "Pro");
    map.put("S", "Ser");
    map.put("T", "Thr");
    map.put("W", "Trp");
    map.put("Y", "Tyr");
    map.put("V", "Val");
    map.put("X", "Ter");
    singleLetterToShortMap = Collections.unmodifiableMap(map);
  }

  public static String shortToSingleLetter(String s){
    return shortToSingleLetterMap.getOrDefault(s.toLowerCase(), null);
  }

  public static String singleLetterToShort(String s){
    return singleLetterToShortMap.getOrDefault(s, null);
  }

  public static String shortToSingleLetterMutation(String m){
    boolean pFlag = m.startsWith("p.");
    m = m.replaceFirst("p.", "");
    Matcher matcher = SHORT_PATTERN.matcher(m);
    while (matcher.find()){
      String matched = matcher.group(0);
      if (shortToSingleLetterMap.containsKey(matched.toLowerCase())){
        m = m.replaceAll(matched, shortToSingleLetterMap.get(matched.toLowerCase()));  
      } else {
        logger.warn(String.format("Unable to identify amino acid symbol %s in variant: %s", matched, m));
      }
    }
    return pFlag ? "p." + m : m;
  }
  
}
