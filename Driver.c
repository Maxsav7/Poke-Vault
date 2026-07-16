/*******************************************************************************
 * @file        Driver.c
 * @brief       Core execution loop and state machine logic for the application
 *
 * @author      John Rosenberger,
 * @date        2026-07-15
 * @copyright   me
 *******************************************************************************/

#include <stdlib.h> //dunno
#include <stdio.h> //for IO
#include <stdint.h> //for int8_t
#include <stdbool.h> //for bool
#include "Card.h" //for Card, OwnedCard, CardArrayList, OwnedArrayList
#include "Search.h" //for searching/filtering/sorting
 

FILE* allCardsFile;
FILE* ownedCardsFile;
CardArrayList cardList;
OwnedCardArrayList ownedList;
int* filteredResults;
int* searchResults;
SortValue sort = NAME; //SortValue is defined in Search.h


//filter is defined in Search.h
FilterBounds filter ={
    //default filter values
    .minPrice = 0.0,
    .maxPrice = 100000000.0, //100 million
    .minValue = 0.0,
    .maxValue = 1000.0,
    .minRarity = 0,
    .maxRarity = MAX_RARITY,
    .minGrade = 1,
    .maxGrade = 10,
    .stage = -1,
    .type = -1,
    .owned = false,
    .unowned = false
};



//does nothing currently
int main(){
    //code goes here 
    //open our saved files and copy them
    //if there's no CardArrayList, gotta initialize it. Probably write another file to do literally only that
    //if there's no OwnedArrayList, make an empty one
    return 0;
}