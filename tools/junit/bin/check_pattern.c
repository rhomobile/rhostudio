#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void check(int condition, int argc, char *argv[]) {
    if (!condition) {
        printf("Error: ");
        int i;
        for (i = 0; i < argc; ++i) {
            printf("%s ", argv[i]);
        }
        puts("");
        exit(1);
    }
}

#define CHECK(c) (check((c), argc, argv))

int main(int argc, char *argv[]) {
    CHECK(argc == 3);

    const char *literal = argv[1];
    const char *pattern = argv[2];

    int len = strlen(pattern);
    CHECK(len % 2 == 0);

    char *l2 = malloc(len / 2 + 1);

    int i;
    for (i = 0; i < len; i += 2) {
        int v;
        sscanf(pattern + i, "%2x", &v);
        l2[i / 2] = (char)v;
    }
    l2[len / 2] = '\0';

    CHECK(strcmp(literal, l2) == 0);

    return 0;
}
