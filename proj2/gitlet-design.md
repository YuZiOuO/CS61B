# Gitlet Design Document

**Name**:CYZ

## Invariant
1. When using Utils.WriteObject(),the object to write should not contain any nested object such file buffer or Blob.
2. In internal class,assume that all arguments passed from main are not null.
3. Classes should only manipulate files in their corresponding directory.
4. Methods in internal classes should atomic,for example they will not support array arguments like String[].

## Note 
1.Should provide a file with a parent dir that already exists to Utils.writeObject(),otherwise you will get an IOException.

## Classes and Data Structures

### Class Main

#### Fields

1. Field 1
2. Field 2

#### Purpose
Check if arguments are valid in form,i.e.
1. the number of arguments is correct
2. the arguments passed to Handler will not cause NullPointerException.

### Class Handler

#### Fields

1. Field 1
2. Field 2

#### Purpose
1. Check if arguments are valid logically, i.e. some specific arguments must have given value such as "--".
2. Invoke any of necessary methods provided by internal classes.

## Algorithms

## Persistence

