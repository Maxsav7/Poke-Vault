#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Card.h"
#include "Data.h"
#include "Misc.h"
#include "Profile.h"
#include "Search.h"
#include "UI.h"

#define INPUT_SIZE 128

static const char* rarityNames[] = {
    "Common", "Uncommon", "Rare", "Holofoil Rare",
    "Double Rare", "Ultra Rare", "Secret Rare"
};

static const char* stageNames[] = {
    "Basic", "Stage 1", "Stage 2"
};

static const char* typeNames[] = {
    "Grass", "Fire", "Water", "Lightning", "Fighting", "Psychic",
    "Colorless", "Darkness", "Metal", "Dragon", "Fairy"
};

static void printLine(void){
    printf("------------------------------------------------------------\n");
}

static void printHeader(const char* title){
    printf("\n");
    printLine();
    printf("                       P O K E V A U L T\n");
    printLine();
    printf("%s\n", title);
    printLine();
}

static void readText(const char* prompt, char* output, size_t size){
    size_t length;
    printf("%s", prompt);
    if(fgets(output, (int)size, stdin) == NULL){
        printf("\n\nInput ended. Closing PokeVault.\n");
        exit(0);
    }
    length = strlen(output);
    if(length > 0 && output[length - 1] == '\n'){
        output[length - 1] = '\0';
    }
}

static int readInt(const char* prompt, int minimum, int maximum){
    char input[INPUT_SIZE];
    char* end;
    long value;

    while(true){
        readText(prompt, input, sizeof(input));
        value = strtol(input, &end, 10);
        if(input[0] != '\0' && *end == '\0' &&
           value >= minimum && value <= maximum){
            return (int)value;
        }
        printf("Please enter a number from %d to %d.\n", minimum, maximum);
    }
}

static double readMoney(const char* prompt){
    char input[INPUT_SIZE];
    char* end;
    double value;

    while(true){
        readText(prompt, input, sizeof(input));
        value = strtod(input, &end);
        if(input[0] != '\0' && *end == '\0' && value >= 0.0){
            return value;
        }
        printf("Please enter a price such as 12.50.\n");
    }
}

static void waitForEnter(void){
    char input[INPUT_SIZE];
    readText("\nPress Enter to continue...", input, sizeof(input));
}

static const char* safeRarity(int value){
    return value >= 0 && value <= 6 ? rarityNames[value] : "Unknown";
}

static const char* safeStage(int value){
    return value >= 0 && value <= 2 ? stageNames[value] : "Unknown";
}

static const char* safeType(int value){
    return value >= 0 && value <= 10 ? typeNames[value] : "Unknown";
}

static void printCardRow(size_t number, const Card* card){
    printf("%2zu. %-22s | %-20s | $%8.2f\n",
           number, card->name, card->set, card->value);
    printf("    %-15s | %-8s | %s\n",
           safeRarity(card->rarity),
           safeStage(card->stage),
           safeType(card->type));
}

static void printOwnedCardRow(size_t number, const OwnedCard* card){
    double change = card->value - card->purchasePrice;
    printf("%2zu. %-22s | %-20s | Grade %d\n",
           number, card->name, card->set, card->grade);
    printf("    Paid $%7.2f | Value $%7.2f | Change %+.2f\n",
           card->purchasePrice, card->value, change);
}

static bool createProfileScreen(char* loggedInUser){
    char username[USERNAME_SIZE];
    char password[PASSWORD_SIZE];
    char confirmPassword[PASSWORD_SIZE];

    printHeader("CREATE PROFILE");
    readText("Choose a username: ", username, sizeof(username));
    readText("Choose a password: ", password, sizeof(password));
    readText("Enter the password again: ", confirmPassword, sizeof(confirmPassword));

    if(strcmp(password, confirmPassword) != 0){
        printf("\nThe passwords did not match.\n");
        waitForEnter();
        return false;
    }

    if(createProfile(username, password)){
        copyString(loggedInUser, username, USERNAME_SIZE);
        printf("\nProfile created. You are now logged in.\n");
        waitForEnter();
        return true;
    }

    printf("\nCould not create the profile. The username may already exist.\n");
    waitForEnter();
    return false;
}

static bool loginScreen(char* loggedInUser){
    char username[USERNAME_SIZE];
    char password[PASSWORD_SIZE];

    printHeader("LOG IN");
    readText("Username: ", username, sizeof(username));
    readText("Password: ", password, sizeof(password));

    if(loginProfile(username, password)){
        copyString(loggedInUser, username, USERNAME_SIZE);
        printf("\nWelcome back, %s.\n", loggedInUser);
        waitForEnter();
        return true;
    }

    printf("\nIncorrect username or password.\n");
    waitForEnter();
    return false;
}

static bool accountScreen(char* loggedInUser){
    int choice;

    while(true){
        printHeader("WELCOME");
        printf("1. Log in\n");
        printf("2. Create profile\n");
        printf("3. Exit\n\n");
        choice = readInt("Choose an option: ", 1, 3);

        if(choice == 1 && loginScreen(loggedInUser)){
            return true;
        }
        if(choice == 2 && createProfileScreen(loggedInUser)){
            return true;
        }
        if(choice == 3){
            return false;
        }
    }
}

static void browseCatalogScreen(CardArrayList* catalog){
    size_t index;

    printHeader("CARD CATALOG");
    if(catalog->size == 0){
        printf("No cards were loaded from data/cards.csv.\n");
    }
    for(index = 0; index < catalog->size; index++){
        printCardRow(index + 1, &catalog->cards[index]);
        printf("\n");
    }
    waitForEnter();
}

static void searchCatalogScreen(CardArrayList* catalog){
    char searchText[INPUT_SIZE];
    int matchCount = 0;
    int* results;
    int index;

    printHeader("SEARCH CATALOG");
    readText("Card name: ", searchText, sizeof(searchText));
    results = searchCards(catalog, searchText, &matchCount);

    printf("\n%d match%s found.\n\n", matchCount, matchCount == 1 ? "" : "es");
    for(index = 0; index < matchCount; index++){
        printCardRow((size_t)results[index] + 1, &catalog->cards[results[index]]);
        printf("\n");
    }
    free(results);
    waitForEnter();
}

static void vaultScreen(const char* username){
    OwnedCardArrayList list;
    size_t index;
    double totalValue = 0.0;
    double totalPaid = 0.0;

    printHeader("MY VAULT");
    if(!loadOwnedCards(username, &list)){
        printf("Could not load your collection.\n");
        waitForEnter();
        return;
    }

    for(index = 0; index < list.size; index++){
        printOwnedCardRow(index + 1, &list.cards[index]);
        printf("\n");
        totalValue += list.cards[index].value;
        totalPaid += list.cards[index].purchasePrice;
    }

    if(list.size == 0){
        printf("Your vault is empty.\n");
    } else {
        printLine();
        printf("Cards: %zu | Paid: $%.2f | Value: $%.2f | Change: %+.2f\n",
               list.size, totalPaid, totalValue, totalValue - totalPaid);
    }

    freeOwnedCardList(&list);
    waitForEnter();
}

static void addCardScreen(const char* username, CardArrayList* catalog){
    int choice;
    double purchasePrice;
    int grade;
    OwnedCard newCard;

    printHeader("ADD CARD TO VAULT");
    for(size_t index = 0; index < catalog->size; index++){
        printf("%2zu. %-24s | %-20s | $%.2f\n",
               index + 1, catalog->cards[index].name,
               catalog->cards[index].set, catalog->cards[index].value);
    }

    choice = readInt("\nChoose a card number: ", 1, (int)catalog->size);
    purchasePrice = readMoney("Purchase price: $");
    grade = readInt("Grade (1-10): ", 1, 10);

    newCard = OwnedCard_init(
        &catalog->cards[choice - 1], "", purchasePrice, (int8_t)grade
    );

    if(appendOwnedCard(username, &newCard)){
        printf("\n%s was added to your vault.\n", newCard.name);
    } else {
        printf("\nThe card could not be saved.\n");
    }
    waitForEnter();
}

static void removeCardScreen(const char* username){
    OwnedCardArrayList list;
    int choice;

    printHeader("REMOVE CARD");
    if(!loadOwnedCards(username, &list)){
        printf("Could not load your collection.\n");
        waitForEnter();
        return;
    }

    if(list.size == 0){
        printf("Your vault is empty.\n");
        freeOwnedCardList(&list);
        waitForEnter();
        return;
    }

    for(size_t index = 0; index < list.size; index++){
        printOwnedCardRow(index + 1, &list.cards[index]);
        printf("\n");
    }

    choice = readInt("Choose a card number to remove: ", 1, (int)list.size);
    if(removeOwnedCardRecord(username, (size_t)(choice - 1))){
        printf("\nCard removed.\n");
    } else {
        printf("\nThe card could not be removed.\n");
    }

    freeOwnedCardList(&list);
    waitForEnter();
}

static void profileScreen(const char* username){
    printHeader("PROFILE");
    printf("Username: %s\n", username);
    printf("Storage: local CSV files in the data folder\n");
    printf("Password: stored as a classroom prototype hash\n");
    waitForEnter();
}

static bool mainMenu(const char* username, CardArrayList* catalog){
    int choice;

    printHeader("HOME");
    printf("Logged in as: %s\n\n", username);
    printf("1. Browse card catalog\n");
    printf("2. Search cards\n");
    printf("3. View my vault\n");
    printf("4. Add a card\n");
    printf("5. Remove a card\n");
    printf("6. View profile\n");
    printf("7. Log out\n\n");

    choice = readInt("Choose an option: ", 1, 7);
    switch(choice){
        case 1: browseCatalogScreen(catalog); break;
        case 2: searchCatalogScreen(catalog); break;
        case 3: vaultScreen(username); break;
        case 4: addCardScreen(username, catalog); break;
        case 5: removeCardScreen(username); break;
        case 6: profileScreen(username); break;
        case 7: return false;
    }
    return true;
}

void runPokeVault(CardArrayList* catalog){
    char loggedInUser[USERNAME_SIZE];
    bool running = true;

    while(running){
        loggedInUser[0] = '\0';
        if(!accountScreen(loggedInUser)){
            running = false;
            continue;
        }

        while(mainMenu(loggedInUser, catalog)){
            /* The selected screen is handled by mainMenu. */
        }
    }

    printf("\nThank you for using PokeVault.\n");
}
