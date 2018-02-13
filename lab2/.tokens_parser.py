#!/usr/bin/python3

import re
import sys
import json

keywords = {"abstract","continue","for","new","switch","assert","default","goto","package","synchronized","boolean","do","if","private","this","break","double","implements","protected","throw","byte","else","import","public","throws","case","enum","instanceof","return","transient","catch","extends","int","short","try","char","final","interface","static","void","class","finally","long","strictfp","volatile","const","float","native","super","while"}
chunks = r'([////]{2}.*?\n)|([////][*].*?[*][////])|\n|(\'\\u[a-fA-F\d]+\')|(\'.{0,1}\')|(".*?")|(\S+)'
ml_comment = r'[////][*].*?[*][////]'
identifiers = r'[a-zA-Z$_][\da-zA-Z$_]*'
literals = r'(\'\\u[a-fA-F\d]+\')|(\'.{0,1}\')|(".*?")|(true)|(false)|(null)|(\d[\d_]*[.][\d_]*(([eE][\-]{0,1}\d){0,1})\d*[dDFf]{0,1})|(0b[01][01_]+([01]|L))|(0x[\da-fA\F][\da-fA-F_]+([\da-fA-F]|L))|(\d[\d_]*L{0,1})|(\d+[L]{0,1})'
parens = r'[\<\>[\[\]\(\)\{\}]'
operators = r'[\+\-\~\!\/\*\%\<\>\=\&\|\:\?\^]'
others = r'[\;\.\,\@]'


def pretify(obj):
  return json.dumps(obj, indent=4)


def totokens(txt):
  tokens = []

  chnks_prog = re.compile(chunks, re.DOTALL)
  line = 0
  linestart = 0
  col = 0
  for m in chnks_prog.finditer(txt):
    chunk = m.group(0)

    linend_match = re.compile(r'([////]{2}.*?\n)|\n', re.DOTALL).match(chunk)
    mlcmnt_match = re.compile(ml_comment, re.DOTALL).match(chunk)

    if linend_match is not None:
      linestart = m.start() + linend_match.end()
      # print('line start: %i' % linestart)
      line += 1
      #print('ignore') 
    elif mlcmnt_match is not None:
      lines = chunk.split('\n')
      lcount = len(lines) - 1
      if lcount > 0:
        line += lcount
        linestart = m.start() + mlcmnt_match.end()
      #print('ignore')
    else:
      #print('lexeme: ok')
      tail = chunk[0:]
      
      while len(tail) is not 0:
        col = m.end() - linestart - len(tail)
        # print('chunk end: %i, tail len: %i, col: %i' % (m.end(), len(tail), col))
        mh = None
        if not re.match(others, tail) is None:
          mh = re.match(others, tail)
          kind = 'other'
        elif not re.match(parens, tail) is None:
          mh = re.match(parens, tail)
          kind = 'paren'
        elif not re.match(operators, tail) is None:
          mh = re.match(operators, tail)
          kind = 'operator'
        elif not re.match(literals, tail) is None:
          mh = re.match(literals, tail)
          kind = 'literal'
        elif not re.match(identifiers, tail) is None:
          if not re.match(identifiers, tail).group(0) in keywords:
            mh = re.match(identifiers, tail)
            kind = 'identifier'
          else:
            mh = re.match(identifiers, tail)
            kind = 'keyword'          
      
        if mh is None:
          # print(pretify(tokens))
          raise ValueError("Parse error - missing %s at %i:%i" % (tail[0], line+1, col+1))
        
        tok = {'lexeme': mh.group(0), 'kind': kind, 'position': (line, col)}
        tokens.append(tok)
        tail = tail[mh.end():]
        # print("token: ", pretify(tok))
        # print("match: ", mh)
    # print('-' * 12)
  return tokens


f = open(sys.argv[1])
txt = f.read()
toks = totokens(txt)
print(pretify(toks))