import sys
def src(name):
    cl = name + "Node"
    o = """package edu.mit.compilers.grammar;


public class """

    o += cl + """ extends DecafNode {

}"""
    f = open(cl + ".java", "w")
    f.write(o)
    f.close()

classes = sys.argv[1].split(",")
for n in classes:
    src(n)