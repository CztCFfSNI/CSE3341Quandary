/*
functional-hw
Name: Zitao Cai
.#: cai.851
course#: 7177
*/

Q main(int arg) {
    Ref list1 = (1 . nil);
    Ref list2 = (2 . nil);
    Ref list3 = ((2 . (3 . nil)) . (2 . (1 . nil)));
    Ref list4 = (4 . (321 . nil));
    print(isList(nil . 1)); 
    print(append(list1, list4));
    print(reverse((3 . (4 . (((56 . (5 . nil)) . nil) . (26 . (2 . ((8 . nil) . nil))))))));
    print(isSorted((3 . (5 . (5 . nil))) . ((2 . (8 . nil)) . ((6 . (7 . (4 . nil))) . ((2 . (3 . (56 . (92 . nil))) . nil))))));
    print(isSorted((3 . (5 . nil)) . ((2 . (8 . nil)) . ((6 . (7 . (4 . nil))) . ((2 . (3 . (56 . (92 . nil))) . nil))))));
    print reverse(list4);
    return 0;
}

int isList(Q obj) {
    if (isAtom(obj) != 0) {
        if (isNil(obj) != 0) return 1;
        return 0;
    }
    return isList(right((Ref)obj));
}

Ref append(Ref list1, Ref list2) {
    if (isNil(list1) != 0) return list2;
    return left(list1) . append((Ref)right(list1), list2);
}

Ref reverse(Ref list) {
    if (isNil(list) != 0) return nil;
    return append(reverse((Ref)right(list)), (left(list) . nil));
}

int length(Ref list) {
    if (isNil(list) != 0) return 0;
    return 1 + length((Ref)right(list));
}

int isSorted(Ref list) {
    if (isNil(list) != 0) return 1;
    Ref rest = (Ref)right(list);
    if (isNil(rest) == 0) {
        if (length((Ref)left(list)) <= length((Ref)left(rest))) return isSorted(rest);
        return 0;
    }
    return 1;
}

/*
5. 
Can’t write functions without an if statement or using recursion. 
The reason is that if statement helps us determine the end case in the recursion progress. 
Recursion does the iterative effect without using loops.

For example, when I try to deal with the append function, 
I use the if statement to first determine whether the list1 that I put into the function is nil. 
If list1 is nil, then that means the progress comes to the end and 
there is no non-nil element left in list1.
Then, that means there is no next left element on the list1 that needs to be appended to the list2. 
Therefore, I can return list2 which is the list that has finished the append progress. 
In this case, I use recursion to keep adding each left element of list1 to list2.
*/
