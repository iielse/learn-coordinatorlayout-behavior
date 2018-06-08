package com.example.learn

class BusEvent {
    constructor(a: Int) {
        act = a
    }

    constructor(a: Int, o: Any) : this(a) {
        obj = o
    }

    constructor(a: Int, o: Any, o2: Any) : this(a, o) {
        obj2 = o2
    }

    constructor(a: Int, o: Any, o2: Any, o3: Any) : this(a, o, o2) {
        obj3 = o3
    }

    var act: Int = 0
    var obj: Any? = null
    var obj2: Any? = null
    var obj3: Any? = null
}