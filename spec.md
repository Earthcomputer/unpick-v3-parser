## Improvements over V2:
- Ability to uninline constants globally (e.g. `Mth.PI`).
    - Also the ability to restrict this to the scope of a package, class, or method (e.g. `ClientboundCustomPayloadPacket.MAX_PAYLOAD_SIZE`).
- Ability to uninline mathematical expressions as well as constants (e.g. `Mth.PI / 3`, `LENGTH - 1`).
- Ability to specify wildcards to uninline all constants in a class.
- Ability to specify the radix of integers (decimal, hex, binary, or octal).
    - This is not strictly constant uninlining but still fits within the broad category of cleaning up constant literals in decompiled code.
- Ability to specify whether an integer should be formatted as a char in decompiled code.

## Example V3 file:
```
unpick v3

# apply to floats in the default group globally
group float
    @strict # don't apply to double literals
    net.minecraft.util.Mth.PI
    net.minecraft.util.Mth.PI / 3

# apply to strings in the default group globally
group String
    net.minecraft.SharedConstants.VERSION_STRING

# an example of a wildcard field
group int LevelEvents
    net.minecraft.world.level.block.LevelEvent.*

# apply to ints in the default group within the scope of ClientboundCustomPayloadPacket
group int
    @scope class net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket 
    net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket.MAX_PAYLOAD_SIZE

# apply to ints with the group "ARGBColor"
group int ARGBColor
    @format hex # format unrecongized constants as hex
    net.minecraft.util.CommonColors.WHITE
    net.minecraft.util.CommonColors.BLACK

# apply the group "ARGBColor" to ints flowing into the 5th parameter of of GuiGraphics.fill
target_method net.minecraft.client.gui.GuiGraphics fill (IIIII)V
    param 4 ARGBColor

# apply the group "ARGBColor" to ints flowing from the return of ARGB.color
target_method net.minecraft.util.ARGB color (IIII)I
    return ARGBColor

# apply to ints with the group "SetBlockFlag"
group int SetBlockFlag
    @flags
    net.minecraft.world.level.block.Block.UPDATE_ALL
    net.minecraft.world.level.block.Block.UPDATE_NEIGHBORS

target_field net.minecraft.core.particles.ColorParticleOption color I ARGBColor
```

## Specification
### General notes
- Any character which is "ignored" may be removed from the file without changing the semantic meaning of the file:
    - Except for the first line, which is the version marker, any `#` character and all subsequent characters until the next new line or the end of the file are ignored.
    - Blank lines (including the new lines that may terminate them) are ignored
### Tokens
```
<Identifier> ::= [a-zA-Z_$] [a-zA-Z0-9_$]*
<Double> ::= [0-9]+ "." [0-9]+ ( ("e" | "E") ("+" | "-")? [0-9]+ )?
<Float> ::= <Double> ("f" | "F")
<Integer> ::= ([1-9] [0-9]*) | (("0x" | "0X") [0-9a-fA-F]+) | (("0b" | "0B") [01]+) | ("0" [0-7]+)
<Long> ::= <Integer> ("l" | "L")
<Char> ::= <JLS §3.10.4 Character Literals, with §3.3 Unicode Escapes being also considered as §3.10.6 Escape Sequences>
<String> ::= <JLS §3.10.5 String Literals, with §3.3 Unicode Escapes being also considered as §3.10.6 Escape Sequences>
<Indent> ::= <lookbehind NewLine> (" " | "\t")+
<NewLine> ::= "\n"   # or the platform new line sequence
<BitShiftLeft> ::= "<<"
<BitShiftRightUnsigned> ::= ">>>"
<BitShiftRight> ::= ">>"
<ClassDescriptor> ::= <JVMS §4.3.2 Field Descriptors>   # This token is a contextual token and is not parsed unless required by the grammar
<MethodDescriptor> ::= <JVMS §4.3.3 Method Descriptors>   # This token is a contextual token and is not parsed unless required by the grammar
<Other> ::= [^]
```
Whitespace is not part of the token stream unless it is part of the `<Indent>` token or `<NewLine>` token. Its only other purpose is to separate tokens that would otherwise be joined together as the same token. Otherwise it is ignored.

### File structure
```
<UnpickV3File> ::= <VersionMarker> (<NewLine> <Item>)*
<VersionMarker> ::= "unpick" "v3"
<Item> ::= <GroupDefinition> | <TargetMethod> | <TargetField>

<GroupDefinition> ::= "group" <DataType> (<Identifier>)? (<NewLine> <Indent> <GroupAttribute>)* (<NewLine> <Indent> <GroupConst>)*

<GroupAttribute> ::= <GroupScope> | <GroupStrict> | <GroupFormat> | <GroupFlags>
<GroupScope> ::= <GroupPackageScope> | <GroupClassScope> | <GroupMethodScope>
<GroupPackageScope> ::= "@" "scope" "package" <ClassName>
<GroupClassScope> ::= "@" "scope" "class" <ClassName>
<GroupMethodScope> ::= "@" "scope" "method" <ClassName> <MethodName> <MethodDescriptor>
<GroupStrict> ::= "@" "strict"
<GroupFormat> ::= "@" "format" <Format>
<Format> ::= "decimal" | "hex" | "binary" | "octal" | "char"
<GroupFlags> ::= "@" "flags"

<GroupConst> ::= <Expression>

# Operator precedence is handled by the lexical structure, which results in the same precedence as in the Java language.
<Expression> ::= <BitOrExpression>
<BitOrExpression> ::= (<BitOrExpression> "|")? <BitXorExpression>
<BitXorExpression> ::= (<BitXorExpression> "^")? <BitAndExpression>
<BitAndExpression> ::= (<BitAndExpression> "&")? <BitShiftExpression>
<BitShiftExpression> ::= (<BitShiftExpression> (<BitShiftLeft> | <BitShiftRight> | <BitShiftRightUnsigned>))? <AdditiveExpression>
<AdditiveExpression> ::= (<AdditiveExpression> ("+" | "-"))? <MultiplicativeExpression>
<MultiplicativeExpression> ::= (<MultiplicativeExpression> ("*" | "/" | "%"))? <UnaryExpression>
<UnaryExpression> ::= <CastExpression> | (("-" | "~")? <PrimaryExpression>)
<CastExpression> ::= "(" <DataType> ")" <UnaryExpression>
<PrimaryExpression> ::= <ParenExpression> | <FieldExpression> | <LiteralExpression>
<ParenExpression> ::= "(" <Expression> ")"
<FieldExpression> ::= <ClassName> "." (<Identifier> | "*") (":" "instance")? (":" <DataType>)?
<LiteralExpression> ::= <Double> | <Float> | <Integer> | <Long> | <Char> | <String>

<TargetMethod> ::= "target_method" <ClassName> <MethodName> <MethodDescriptor> (<NewLine> <Indent> <TargetMethodItem>)*
<TargetMethodItem> ::= <TargetMethodParam> | <TargetMethodReturn>
<TargetMethodParam> ::= "param" <Integer> <Identifier>
<TargetMethodReturn> ::= "return" <Identifier>

<TargetField> ::= "target_field" <ClassName> <Identifier> <ClassDescriptor> <Identifier>

<DataType> ::= "byte" | "short" | "int" | "long" | "float" | "double" | "char" | "String" | "Class"
<ClassName> ::= (<Identifier> ".")* <Identifier>
<MethodName> ::= ("<" "init" ">") | ("<" "clinit ">") | <Identifier>
```

### Semantics
#### Group Definitions
- A group definition defines a group, or adds to its definition if the group is already defined.
- A group's data type specifies the narrowest type of literals to which the constant replacements specified within this group may be applied.
    - If the group has the `@strict` attribute, then constant replacement only occurs on literals of the group's exact data type. Otherwise, constant replacement additionally occurs in literals whose type the group's data type may be converted to via widening primitive conversion (JLS §5.1.2).
    - The group data type may be `int`, `long`, `float`, `double`, `String`, or `Class`. It cannot be `byte`, `short`, or `char` owing to the lack of literals of these types in bytecode.
- If the group name is missing, then the group definition adds to the definition of the default group.
- A group can have attributes, which modify the behavior of the group in some way.
    - The `@flags` attribute specifies that a group is made up of bitflags that may be combined using bitwise operators to make up a literal.
        - This attribute is only applicable to groups of type `int` and `long`.
        - This attribute is only applicable to named groups, not the default group.
    - The `@format` attribute specifies how a literal should be formatted in source code.
        - The `decimal` and `hex` formats are applicable to the `int`, `long`, `float`, and `double` group types.
        - The `binary`, `octal`, and `char` formats are applicable to the `int` and `long` group types.
    - The `@strict` attribute causes the group to only be applied to literals of the exact type, as explained above.
    - The `@scope` attribute adds a restriction on which literals are subject to replacement by the constant replacements in this group declaration. Only literals within the given package, class, or method can be replaced with these constants.
        - Multiple `@scope` declarations can appear on a group definition. In this case, the scopes are ORed together, such that a literal can appear in any of the given scopes to be replaced by a listed constant replacement.
        - Package scopes are not recursive; subpackages are not included. For example, a package scope for `foo` includes the class `foo.A` but not `foo.bar.A`. This is because the Java language and JVM do not treat packages as a hierarchy; subpackage relationships exist only in the directory structure, not in the language or runtime semantics.
- It is an error for the same group to specify two constants that evaluate (see below) to the same value in the same scope.
    - If the same group specifies the same constant but at different scopes, the most specific scope takes precedence.
- Each expression must evaluate (see below) to the group data type, or be convertible to the group data type via widening primitive conversion (JLS §5.1.2).
    - If the group data type is `String` or `Class`, the expression may also be a field expression which evaluates to `null`. In this case, the resolved field must have the same type as the group type.
#### Expressions
- The semantics of all operators are the same as in the Java language. The `+` operator handles both addition and string concatenation, as in Java. The cast operator cannot be used to convert between strings and other types.
- Operators perform widening primitive conversion (JLS §5.1.2) even when casts aren't explicitly specified, like they do in Java. This includes unary operators on the `byte`, `short`, and `char` types.
- If a widening conversion is applied to a literal expression, then the type of the literal expression is changed to the widened type during constant substitution.
- It is an error to use an operator on a data type that it would be incompatible with in Java.
- If the data type of a field expression is unspecified, it defaults to the group data type if the field expression is a wildcard, and the data type of the resolved field otherwise.
- For non-static constant fields, an `:instance` suffix can be added to a field expression.
    - For unqualified instance field expressions in the Java source code, i.e. those without an explicit `this` or `OuterClass.this` qualifier, there is no remnant of the instance in the bytecode, and the unpick implementation may assume that the instance is coming from `this` or a (possibly nested) outer class reference.
    - For qualified instance field expressions in the Java source code, the instance of a constant expression is retained in the bytecode and the field access is replaced with a null check. Additionally, the expression isn't simplified further by the Java compiler. A bytecode implementation of unpick could implement non-static field uninlining by looking for the following pattern in bytecode:
        - In Java 8 and below:
            - `invokevirtual java/lang/Object.getClass()Ljava/lang/Class; pop; <load constant>`
        - In Java 9 and above:
            - `invokestatic java/util/Objects.requireNonNull(Ljava/lang/Object;)Ljava/lang/Object; pop; <load constant>`
        - and replacing it with a `getfield` instruction, which should preserve the instance which should have been loaded onto the stack already before the null check. Implementations should check that the type on the top of the stack is compatible with the `getfield` instruction that is added.
- Wildcard field expressions resolve to a list of matching fields in the class specified by the expression. Unpick implementations shall behave as if a separate group constant existed for each resolved field, identical to the original except with the wildcard replaced by the specific field.
    - This implies that it is an error for any of these expressions to evaluate to the same value as each other (or any other expression in the group with the same scope).
    - If the data type is specified in the field expression, then a field is matching if its data type is equal to the specified type, and its staticness matches the staticness of this field expression (static unless the `:instance` suffix is present).
    - If the data type is not specified in the field expression, then a field is matching if its data type is equal to the group data type, or is convertible to it via widening primitive conversion (JLS §5.1.2), and its staticness matches the staticness of this field expression (static unless the `:instance` suffix is present).
- No more than one wildcard field expression is allowed per group constant.
#### Targets
- Target method parameters are indexed starting from 0.
    - Parameters are *not* indexed by their bytecode local variable index as with some mapping formats. All parameters take 1 index.
- Targets must be a compatible type with the type of the group they specify.
    - `byte`, `short`, `int`, `long`, `float`, and `double` are all compatible with each other.
    - `char` is compatible with `byte`, `short`, `int`, and `long`.
    - `String` and `Class` are only compatible with themselves.
#### Class Names
- Class names use a class' binary name (JLS §13.1), which uses `.` to separate package elements and `$` to separate inner class names from outer class names. This format was chosen over the internal name to avoid potential confusion with the `/` division operator.

## Semantic Verification
It may be useful to perform the following steps for semantic verification of unpick v3 files:
- For each scope, validate that that scope points to a package, class, or method that exists.
- For each field expression, validate that it references an existing field of the (possibly implicit) type and staticness specified in the unpick file.
    - Also check that this field is `final` and is initialized to a compile time constant (JLS §15.28).
    - If the field expression is a wildcard field expression, validate that it has at least one matching field (see above).
- Evaluate each expression according to the rules of the Java language.
    - For each occurrence of the `/` and `%` operators, validate that if both sides of the operator are integers, the right hand side does not evaluate to 0.
    - Check that every expression in each group does not evaluate to the same value as another expression in the same group with the same scope.
    - Check that every expression evaluates to a value of a compatible type to the group type (see above).
- Check the existence of target fields and methods.
    - Check that target parameters are not out of bounds.

## Application
Note that this section is only one example of how constant uninlining could be implemented with these files. Implementations are free to uninline in less or more places than specified here or to use different techniques. This section is meant to give a feel for how the file format is supposed to be interpreted and reasoned about, not to dictate how the implementation is supposed to look.

Following is a description of an algorithm to uninline constants in a method at the source code level. In practice it may be undesirable to only uninline a single method in isolation, due to the existence of inner methods (via anonymous or local classes), and parts of the method which is outside the body in the source code but inside in the bytecode (e.g. field assignments in both `<init>` and `<clinit>`).

### Identify targets
Every expression and sub-expression in the syntax tree of the method body is associated to a group. In addition, every parameter and local variable is associated to a group. All expressions and variables are initially assigned to the default group. Then:

#### Enclosing method parameters
Search for if the enclosing method or any method it overrides or implements is a target method in the unpick file. For each target parameter, assign the group of that parameter in the method to the group specified in the unpick file.

#### Enclosing method return
Search for if the enclosing method or any method it overrides or implements is a target method in the unpick file. If the group of the return value of the target method is specified, then assign the group of every expression which is the argument of a return statement to the group specified in the unpick file.

#### Referenced fields
For every field reference in the method, search for if the referenced field is a target field in the unpick file. If it is, assign the group of the field reference expression to the group specified in the unpick file.

#### Referenced method parameters
For every method call expression and `new` expression in the method, search for if the referenced method or constructor is a target method in the unpick file, or if the referenced method overrides or implements a target method in the unpick file. For each parameter of that method call, assign the group of the expression passed as that parameter to the group specified in the unpick file, if present.

#### Referenced method returns
For every method call expression in the method, search for if the referenced method, or any method it overrides or implements, is a target method in the unpick file. If it is, and the group of the return value of the target method is specified, then assign the group of the method call expression to the group specified in the unpick file.

#### Pattern variables
For all variables declared as a pattern variable from destructuring a record, search for if the referenced field or accessor method is a target field or method in the unpick file. If it is, assign the group of the variable to the group specified in the unpick file.

### Propagate groups
In this step, the non-default groups of expressions are repeatedly propagated to other expressions until there are no further propagations to apply. If propagation assigns a group to an expression, but that expression is already assigned to a different non-default group, this is a group conflict. How to handle group conflicts is up to the implementation, but throwing an error or warning or assigning an arbitrary group are possible implementations.

#### Variable propagation
If a variable is assigned a group, then that group is propagated to all references to that variable. If a variable reference is assigned a group, then that group is assigned to all other references to that variable and the variable itself.

#### Variable declaration propagation
The group of a variable and the expression it is assigned to as part of its declaration are propagated to each other.

#### Operator propagation
- For the following binary operators, the group of the left hand side, right hand side, and the whole expression are propagated to the other two expressions: `=`, `+=`, `-=`, `*=`, `&=`, `|=` `^=`, `+`, `-`, `*`, `&`, `|`, `^`.
- For the following binary operators, the group of the left hand side and the whole expression are propagated to each other: `/=`, `%=`, `>>=`, `>>>=`, `<<=`, `/`, `%`.
- For the following binary operators, the group of the left and side and right and side are propagated to each other: `==`, `!=`, `>`, `<`, `>=`, `<=`.
- For the following unary operators, the group of the operator and the whole expression are propagated to each other: casts, `+`, `-`, `~`, `++` (prefix and postfix), `--` (prefix and postfix).
- For the ternary operator, the type of the true expression, false expression, and the whole expression are propagated to the other two expressions.

#### Switch propagation
- The group of the subject of a switch statement or expression is propagated to all the case labels of that switch statement or expression.
- In switch expressions, the group of each expression value of a label, the expression argument to each yield statement, and the switch expression itself are propagated to each other.

### Substitute constants
For each literal expression, field access expression, and class object access expression in the method body:
- If the expression is a field access expression, and the field is the `TYPE` field of one of the boxed primitive types, then apply the following steps as if the expression is a class object access expression for the corresponding primitive type. Otherwise if the expression is a field access expression, skip this expression.
- If the literal expression is directly inside a unary `-` expression, then apply the following steps on the unary expression rather than on the literal itself.
- If the group doesn't have the `@flags` attribute, or if the group has the `@flags` attribute and the literal is 0 or -1: if there are any substitutions (matching the scope of the current method) matching the value of the literal, then replace that literal expression with the expression specified by that substitution.
- If the group has the `@flags` attribute and the literal is not 0 or -1:
    - Find the minimal set of substitutions (matching the scope of the current method) (flags may cover more than one bit, so the "minimal set" part is important here), where the evaluated value of those substitutions, when bitwise ORed together, produce as many bits of the literal as possible; let this set be called the "positive set" and the leftover bits not produced be called the "residual".
    - Find the minimal set of substitutions (matching the scope of the current method), where the evaluated value of those substitutions, when bitwise ORed together, produce the bitwise inverse of the literal; let this set be called the "negative set", there may be no such set.
    - If the negative set exists, and either the residual is not 0 or the negative set is smaller than the positive set, then replace the literal expression with `~(expr1 | expr2 | ...)` where `expr1`, `expr2`, etc are the expressions specified by the substitutions in the negative set.
    - Otherwise, replace the literal expression with `expr1 | expr2 | ... | residual`, where `expr1`, `expr2`, etc are the expressions specified by the substitutions in the positive set. Do not include the residual if it is 0, otherwise apply the format of the group to the residual if specified.
- If the literal remains unsubstituted, apply the format of the group to the literal.
- If the substituted expression is directly inside a cast expression, and the type (JLS §15) of the substituted expression is equal to the type of the cast expression, or is convertible to it via widening primitive conversion (JLS §5.1.2), then the cast *may* be replaced by the substituted expression. The cast cannot be replaced if it changes semantics, for example by changing the overload of a method call. A cast may also need to be added for the same reason.

### A note on field initializers
An unpick implementation must be careful not to substitute constants in a field initializer in a way that would cause the code not to compile, or the field to no longer be a constant expression. This involves:
- Not referencing fields that are declared later in the same class.
- Not creating a cyclical reference of fields.

A simple way to circumvent the issue is to not substitute constants in a field initializer if the field being initialized is a constant, or if the substituted expression contains field expressions referencing fields that are further down in the same class.

If an unpick implementation wishes to go further, then the problems may be resolved by applying a different constant group to the offending field initializer than would normally be applied:
- Constants from a wider scope may instead be applied. For example, if substituting a constant in the class scope would lead to a cyclical reference, but a constant from the global scope is also applicable and would not lead to a cyclical reference, then the constant from the global scope can be applied.
- If the group is not the default group, then the default group can also be attempted.
- If the group is the default group, then a constant for a wider but still compatible data type could be applied, provided that constant is not marked as `@strict`.
