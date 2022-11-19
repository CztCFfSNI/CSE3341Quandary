mutable int main (int arg) {
    if (arg == 1) MarkSweep(16);
    if (arg == 2) RefCount(666);
    if (arg == 3) ExplicitMemoryManagement(16);
    if (arg == 4) RefCount(9);
    return 0;
}
 
mutable Ref MarkSweep(mutable int n) {
    mutable Ref r1 = nil . nil;
    mutable Ref r2 = r1;
    while (n > 0) {
        n = n - 1;
        setRight(r2, nil . nil);
        r2 = (Ref)right(r2);
    }
    return nil;
}

mutable int RefCount(mutable int n) {
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
    mutable Ref r1 = nil . nil;
    mutable Ref r2 = r1;
    while (n > 0) {
        n = n - 1;
        setRight(r2, nil . nil);
        r2 = (Ref)right(r2);
        free(r2);
    }
    return nil;
}