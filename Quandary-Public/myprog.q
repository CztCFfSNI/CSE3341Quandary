mutable int main (int arg) {
    if (arg == 1) MarkSweep(16);
    if (arg == 2) RefCount(9999);
    if (arg == 3) ExplicitMemoryManagement(16);
    if (arg == 4) RefCount(10);
    return 0;
}
 
mutable Ref MarkSweep(mutable int n) {
    if (n < 0) n = 0;
    Ref r = nil . nil;
    mutable Ref temp = r;
    while (n > 0) {
        n = n - 1;
        setRight(temp, (nil . nil));
        temp = (Ref)right(temp);
    }
    return nil;
}

mutable int RefCount(mutable int n) {
    if (n < 0) n = 0;
    while (n > 0) {
        n = n - 1;
        Ref r1 = nil . nil;
        Ref r2 = nil . nil;
        setRight(r1, r2);
        setRight(r2, r1);
    }
    return 1;
}

mutable Ref ExplicitMemoryManagement(mutable int n) {
    if (n < 0) n = 0;
    Ref r = nil . nil;
    mutable Ref temp = r;
    while (n > 0) {
        n = n - 1;
        setRight(temp, (nil . nil));
        temp = (Ref)right(temp);
        free(temp);
    }
    return nil;
}