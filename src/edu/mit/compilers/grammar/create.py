import sys
def src(name):
    cl = name + "Node"
    o = """package edu.mit.compilers.grammar;


public class """

    o += cl + """ extends DecafNode {

}"""
    f = open(c + ".java", "w")
    f.write(o)
    f.close()
src(sys.argv[1])
