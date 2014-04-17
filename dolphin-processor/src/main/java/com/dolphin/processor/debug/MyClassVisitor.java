package com.dolphin.processor.debug;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MyClassVisitor extends ClassVisitor {
        public MyClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM4, cv);
        }
        
        @Override
        public void visit(int version, int access, String name,
        		String signature, String superName, String[] interfaces) {
        	//super.visit(version, access, name, signature, superName, interfaces);
        	
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) { return null; }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                String signature, String[] exceptions) {
        	
        	return new MyTraceMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
        }
    }