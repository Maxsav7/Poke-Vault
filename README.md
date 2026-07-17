# Poke-Vault
team introduction and roles: https://docs.google.com/document/d/1J76XzlaMxGujWU550qgCmzxnHcvwRGraevUzeY3TJHI/edit?pli=1&tab=t.0#heading=h.k7n46nj9s2b3
project proposal: https://docs.google.com/document/d/1jH55MXtAWYvGU7Tz4a1WKWfXuAmSSTvxAypVSZjqihk/edit?tab=t.0#heading=h.p9no7i48sioa
prototype flow document: https://xd.adobe.com/view/11bde966-4510-45cd-a5ad-3803dcae36ed-44ee/



**********************************************************************
files
**********************************************************************

###PokeVault.sh
  //just a file that runs gcc on all these C files and then tries to run the newly created gcc program.



###Driver.c
  //the file that runs all of our logic and calls the different functions
  ***fields***
    *`FilterBounds filter`
      //has default values assigned
  ***methods***
    *`int main()`
      //does nothing
   

  
###Card.h
  ***structs***
    *`Card`
      *`char name[50]` //longest real card name is like 30 characters long
      *`char set[100]` //longest set name is 58 characters long... tracks the set this card was FIRST released for
      *`double value` //market value
      *`int8_t rarity` //0 = common,   1 = uncommon,   2 = rare,   3 = holofoil rare,   4 = double rare,   5 = ultra rare,   6 = secret rare
      *`int8_t stage` //0 = basic,   1 = stage 1,   2 = stage 2
      *`int8_t type` //0=grass, 1=fire, 2=water, 3=lightning, 4=fighting, 5=psychic, 6=colorless, 7=darkness, 8=metal, 9=dragon, 10=fairy
      *`bool owned` //tracks whether the user owns any copies of this particular card
      //int8_t is just a smaller int
    *`OwnedCard`
      *`char name[50]` //longest real card name is like 30 characters long
      *`char set[100]` //longest set name is 58 characters long... tracks the set this exact card came from
      *`double value` //market value
      *`int8_t rarity` //0 = common,   1 = uncommon,   2 = rare,   3 = holofoil rare,   4 = double rare,   5 = ultra rare,   6 = secret rare
      *`int8_t stage` //0 = basic,   1 = stage 1,   2 = stage 2
      *`int8_t type` //0=grass, 1=fire, 2=water, 3=lightning, 4=fighting, 5=psychic, 6=colorless, 7=darkness, 8=metal, 9=dragon, 10=fairy
      *`double purchasePrice`; //how much this exact card was purchased for
      *`int8_t grade`; //1-10
      //int8_t is just a smaller int
    *`CardArrayList` //a <Card> ArrayList, incomplete. Lacks functions other than size.
    *`OwnedCardArrayList` //an <OwnedCard> ArrayList, incomplete. Lacks functions other than size.
    
  ***methods***
    *`Card card_init(const char* name, const char* set, double value, int8_t rarity, int8_t stage, int8_t type)`
      //implemented in Card.c
    *`OwnedCard OwnedCard_init(Card* card, const char* set, double purchasePrice, int8_t grade)`
      //implemented in Card.c
    
    

###Card.c
  ***methods***
    *Card card_init(const char* name, const char* set, double value, int8_t rarity, int8_t stage, int8_t type)`
      //creates a new Card and assigns these values to it. The owned field has a default value of false. Returns the created card so you can store it.
  
    *`OwnedCard OwnedCard_init(Card* card, const char* set, double purchasePrice, int8_t grade)`
      //creates a new OwnedCard and assigns these values to it. If an empty string is sent for set, assigns the corresponding Card's set to the OwnedCard. Returns the OwnedCard so you can store it.
  
  

###Search.h
  ***enums***
    *`SortValue`
      *NAME, PRICE, VALUE, RARITY, SET. //Price doesn't apply to CardArrayList, only to OwnedCardArrayList.
  
  ***structs***
    *`FilterBounds`
      *min/max (purchase) price, min/max (market) value, min/max rarity, min/max grade, stage, type, owned, and unowned.
  
  ***methods***
    *bool filterCard(const Card* card, FilterBounds bounds); //returns true if the Card passes the filter
    *bool filterOwnedCard(const OwnedCard* card, FilterBounds bounds); //returns true if the OwnedCard passes the filter
    *int* searchCards(const CardArrayList* library, const char* name, int* matchCount);//returns an int array for the indexes of matches
    *int* searchOwnedCards(const OwnedCardArrayList* library, const char* name, int* matchCount);//returns an int array for the indexes of matches
    *int* sortAllCards(int* searchResults, int* matchCount, SortValue sort, bool descending);//sorts and returns a global int array
    *int* sortOwnedCards(int* searchResults, int* matchCount, SortValue sort, bool descending);//sorts and returns a global int array
    //all of these are defined in Search.c
    


###Search.c
  ***fields***
    *`static const CardArrayList* cardLibrary`;
    *`static const OwnedCardArrayList* ownedCardLibrary`;
    *`static bool sortDescending` = false;
    *`int* searchResults`;

  ***filter methods***
    *`bool filterCard(const Card* card, FilterBounds bounds)`
      //returns true if the Card passes the filter
    *`bool filterOwnedCard(const OwnedCard* card, FilterBounds bounds)`
      //returns true if the OwnedCard passes the filter
    *`int* changeFilter(const CardArrayList* library, int* searchResults, FilterBounds bounds, int matchCount, int* filteredCount)`
      //returns a filtered int array for the indexes of matches
  
  ***search methods***
    `int* searchCards(const CardArrayList* library, const char* name, int* matchCount)`
      //linear search, returns an int array for the indexes of matches.
      //assigns `library` to `cardLibrary` in Search.c
      //save `matchCount` before passing it to `searchCards`, this int* is important to track.
    `int* searchOwnedCards(const OwnedCardArrayList* library, const char* name, int* matchCount)`
      //linear search, returns an int array for the indexes of matches
      //assigns `library` to `ownedCardLibrary` in Search.c
      //save `matchCount` before passing it to `searchCards`, this int* is important to track.
  
  ***comparison methods***
    //these are only for the sorters
    *`int compName(const void* first, const void* second)`
    *`int compSet(const void* first, const void* second)`
    *`int compValue(const void* first, const void* second)`
    *`int compRarity(const void* first, const void* second)`
    *`int compOwnedName(const void* first, const void* second)`
    *`int compOwnedSet(const void* first, const void* second)`
    *`int compOwnedPrice(const void* first, const void* second)`
    *`int compOwnedValue(const void* first, const void* second)`
    *`int compOwnedRarity(const void* first, const void* second)`
    
  ***sort methods***
    *`int* sortAllCards(int* searchResults, int* matchCount, SortValue sort, bool descending)`
      //sorts and returns an int array passed to it
      //writes to the sortDescending field in Search.c
    *`int* sortOwnedCards(int* searchResults, int* matchCount, SortValue sort, bool descending)`
      //sorts and returns an int array passed to it
      //writes to the sortDescending field in Search.c
  
  

###Misc.h
  ***methods***
    //these are all defined in Misc.c
    *char* upperCase(const char* lowercase)
    *void copyString(char* output, const char* input, int size)

###Misc.c
  ***methods***
    *`char* upperCase(const char* lowercase)`
      //pass it a string, it returns the uppercase version of that string. Don't forget to free()!
    *`void copyString(char* output, const char* input, int size)`
      //literally just strncpy but better
