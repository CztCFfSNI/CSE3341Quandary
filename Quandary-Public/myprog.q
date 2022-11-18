mutable int main (int arg) {
    if (arg == 1) MarkSweep(16);
    else if (arg == 2) RefCount(1000);
    else if (arg == 3) ExplicitMemoryManagement(20);
    else if (arg == 4) RefCount(12);
    else print(arg);
    return 1776;
}
 
mutable Ref MarkSweep(mutable int n) {
    if (n < 0) n = 0;
    Ref r = (nil . nil);
    mutable Ref temp = r;
    while (n > 0) {
        setRight(temp, (nil . nil));
        temp = (Ref)right(temp);
        n = n - 1;
    }
    return r;
}

mutable int RefCount(mutable int n) {
    if (n < 0) n = 0;
    while (n > 0) {
        Ref a = (1 . nil);
        Ref b = (2 . nil);
        setRight(a, b);
        setRight(b, a);
        n = n - 1;
    }
    return 1776;
}

mutable Ref ExplicitMemoryManagement(mutable int n) {
    if (n < 0) n = 0;
    Ref r = (nil . nil);
    mutable Ref temp = r;
    while (n > 0) {
        setRight(temp, (nil . nil));
        Ref oldTemp = temp;
        temp = (Ref)right(temp);
        free(oldTemp);
        n = n - 1;
    }
    return r;
}