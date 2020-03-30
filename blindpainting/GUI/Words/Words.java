/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.Words;

import java.util.Random;

/**
 *
 * @author Nick Berryman
 */
public class Words {
    private static final String[] easy = 
            ("cat"
            + ",sun"
            + ",cup"
            + ",ghost"
            + ",flower"
            + ",pie"
            + ",cow"
            + ",banana"
            + ",snowflake"
            + ",bug"
            + ",book"
            + ",jar"
            + ",snake"
            + ",light"
            + ",tree"
            + ",lips"
            + ",apple"
            + ",slide"
            + ",socks"
            + ",smile"
            + ",swing"
            + ",coat"
            + ",shoe"
            + ",water"
            + ",heart"
            + ",hat"
            + ",ocean"
            + ",kite"
            + ",dog"
            + ",mouth"
            + ",milk"
            + ",duck"
            + ",eyes"
            + ",skateboard"
            + ",bird"
            + ",boy"
            + ",apple"
            + ",person"
            + ",girl"
            + ",mouse"
            + ",ball"
            + ",house"
            + ",star"
            + ",nose"
            + ",bed"
            + ",whale"
            + ",jacket"
            + ",shirt"
            + ",hippo"
            + ",beach"
            + ",egg"
            + ",face"
            + ",cookie"
            + ",cheese"
            + ",ice cream cone"
            + ",spoon"
            + ",worm"
            + ",spider web"
            + ",bridge"
            + ",bone"
            + ",grapes"
            + ",bell"
            + ",jellyfish"
            + ",bunny"
            + ",truck"
            + ",grass"
            + ",door"
            + ",monkey"
            + ",spider"
            + ",bread"
            + ",ears"
            + ",bowl"
            + ",bracelet"
            + ",alligator"
            + ",bat"
            + ",clock"
            + ",lollipop"
            + ",moon"
            + ",doll"
            + ",orange"
            + ",ear"
            + ",basketball"
            + ",bike"
            + ",airplane"
            + ",pen"
            + ",inchworm"
            + ",seashell"
            + ",rocket"
            + ",cloud"
            + ",bear"
            + ",corn"
            + ",chicken"
            + ",purse"
            + ",glasses"
            + ",blocks"
            + ",carrot"
            + ",turtle"
            + ",pencil"
            + ",horse"
            + ",dinosaur"
            + ",head"
            + ",lamp"
            + ",snowman"
            + ",ant"
            + ",giraffe"
            + ",cupcake"
            + ",chair"
            + ",leag"
            + ",bunk bed"
            + ",snail"
            + ",baby"
            + ",balloon"
            + ",bus"
            + ",cherry"
            + ",crab"
            + ",football"
            + ",branch"
            + ",robot").split(",");
    
    public static String generatePrompt(){
        Random rand = new Random();
        int index = rand.nextInt(easy.length);
        return easy[index];
    }
}
