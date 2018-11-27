package com.squareup.wire.java;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.wire.schema.Field;
import com.squareup.wire.schema.ProtoFile;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by weijiangnan on 2018/11/27.
 */

public class Utils {
    public static String getProtoFileClassName(ProtoFile protoFile) {
        StringBuilder sb = new StringBuilder();
        sb.append(protoFile.name().substring(0, 1).toUpperCase());
        sb.append(protoFile.name().substring(1, protoFile.name().length()));
        sb.append("Protos");

        return sb.toString();
    }

    public static MethodSpec getter(FieldSpec fieldSpec) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(getFieldGetterName(fieldSpec));

        if (fieldSpec.type.equals(TypeName.BOOLEAN.box())) {
            builder.addStatement("if($N == null) return false", fieldSpec.name);
            builder.addStatement("return $N", fieldSpec.name);
            builder.returns(TypeName.BOOLEAN);
        } else if (fieldSpec.type.equals(TypeName.BYTE.box())) {
            builder.addStatement("if($N == null) return 0", fieldSpec.name);
            builder.addStatement("return $N", fieldSpec.name);
            builder.returns(TypeName.BYTE);
        } else if (fieldSpec.type.equals(TypeName.SHORT.box())) {
            builder.addStatement("if($N == null) return 0", fieldSpec.name);
            builder.addStatement("return $N", fieldSpec.name);
            builder.returns(TypeName.SHORT);
        } else if (fieldSpec.type.equals(TypeName.INT.box())) {
            builder.addStatement("if($N == null) return 0", fieldSpec.name);
            builder.addStatement("return $N", fieldSpec.name);
            builder.returns(TypeName.INT);
        } else if (fieldSpec.type.equals(TypeName.LONG.box())) {
            builder.addStatement("if($N == null) return 0L", fieldSpec.name);
            builder.addStatement("return $N", fieldSpec.name);
            builder.returns(TypeName.LONG);
        } else if (fieldSpec.type.equals(TypeName.FLOAT.box())) {
            builder.addStatement("if($N == null) return 0f", fieldSpec.name);
            builder.addStatement("return $N", fieldSpec.name);
            builder.returns(TypeName.FLOAT);
        } else if (fieldSpec.type.equals(TypeName.DOUBLE.box())) {
            builder.addStatement("if($N == null) return 0.0", fieldSpec.name);
            builder.addStatement("return $N", fieldSpec.name);
            builder.returns(TypeName.DOUBLE);
        } else {
            builder.addStatement("return $N", fieldSpec.name);
            builder.returns(fieldSpec.type);
        }

        builder.addModifiers(PUBLIC);

        return builder.build();
    }

    public static String getFieldGetterName(FieldSpec fieldSpec) {
        String name = fieldSpec.name;
        StringBuilder sb = new StringBuilder();
        sb.append(name.substring(0, 1).toUpperCase());
        for (int i = 1; i < name.length(); i++) {{
            if (name.charAt(i) == '_' && i < name.length()) {
                // do nothing
            } else if (i - 1 > 0 && name.charAt(i - 1) == '_') {
                sb.append(name.substring(i, i + 1).toUpperCase());
            } else {
                sb.append(name.charAt(i));
            }
        }}

        if (fieldSpec.type.equals(TypeName.BOOLEAN.box())
                && (name.startsWith("is") || name.startsWith("has"))) {
            // do nothing
        } else {
            sb.insert(0, "get");
        }

        if (fieldSpec.type.toString().contains("java.util.List")) {
            if (!name.endsWith("List")) {
                sb.append("List");
            }
        }

        return sb.toString();
    }

    public static MethodSpec setter(FieldSpec fieldSpec) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(getFieldSetterName(fieldSpec));
        builder.addParameter(fieldSpec.type, fieldSpec.name);
        builder.addStatement("this.$N = $N", fieldSpec.name, fieldSpec.name);
        builder.addStatement("return this");
        builder.returns(fieldSpec.type);
        builder.addModifiers(PUBLIC);

        return builder.build();
    }

    public static String getFieldSetterName(FieldSpec fieldSpec) {
        String name = fieldSpec.name;
        StringBuilder sb = new StringBuilder();
        sb.append(name.substring(0, 1).toUpperCase());
        for (int i = 1; i < name.length(); i++) {{
            if (name.charAt(i) == '_' && i < name.length()) {
                // do nothing
            } else if (i - 1 > 0 && name.charAt(i - 1) == '_') {
                sb.append(name.substring(i, i + 1).toUpperCase());
            } else {
                sb.append(name.charAt(i));
            }
        }}

        sb.insert(0, "set");
        if (fieldSpec.type.toString().contains("java.util.List")) {
            if (!name.endsWith("List")) {
                sb.append("List");
            }
        }

        return sb.toString();
    }

    public static String getFieldSetterName(Field field) {
        String name = field.name();
        StringBuilder sb = new StringBuilder();
        sb.append(name.substring(0, 1).toUpperCase());
        for (int i = 1; i < name.length(); i++) {{
            if (name.charAt(i) == '_' && i < name.length()) {
                // do nothing
            } else if (i - 1 > 0 && name.charAt(i - 1) == '_') {
                sb.append(name.substring(i, i + 1).toUpperCase());
            } else {
                sb.append(name.charAt(i));
            }
        }}

        sb.insert(0, "set");
        if (field.type().toString().contains("java.util.List")) {
            if (!name.endsWith("List")) {
                sb.append("List");
            }
        }

        return sb.toString();
    }
}
