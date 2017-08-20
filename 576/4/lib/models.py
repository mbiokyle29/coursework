"""
models.py
author: Kyle McChesney (kgmcchesney)

Hidden Markov Model Implementation
HMM: Top level object
State: Graph Node, with adj list edges
"""
import logging
from math import log as logrthm
log = logging.getLogger(__name__)

class HMM(object):

    def __init__(self):

        # initalize states
        self.start = State("start")
        self.end = State("end")
        self.exon = State("exon", emissions=['A','T','C','G'])
        self.intron = State("intron", emissions=['a','t','c','g'])
        self.ALPHABET_SIZE = 4

        # add the edges from start
        self.start.add_edge(self.exon, 1.0)
        
        # exon state
        self.exon.add_edge(self.exon, 0.0)
        self.exon.add_edge(self.intron, 0.0)
        self.exon.add_edge(self.end, 0.0)

        # intron state   
        self.intron.add_edge(self.exon, 0.0)
        self.intron.add_edge(self.intron, 0.0)

    def train(self, train_data):

        # we need to store 
        # counts for each edge
        # counts for each seq emission (per state)
        exon_exon = 1
        exon_intron = 1
        exon_end = len(train_data) + 1

        intron_exon = 1
        intron_intron = 1

        exon_emit = {"A":1, "T":1, "C":1, "G": 1}
        intron_emit = {"a":1, "t":1, "c":1, "g":1 }

        seen = 0

        for seq in train_data:

            state = "start"
            for char in seq:
                seen += 1 

                # upper case
                if char.isupper():

                    if state == "intron":
                        intron_exon += 1
                        state = "exon"

                    elif state == "start":
                        state = "exon"

                    else:
                        exon_exon += 1

                    # emit the char
                    exon_emit[char] += 1
                
                # lower case
                else:

                    if state == "exon":
                        exon_intron += 1
                        state = "intron"

                    else:
                        intron_intron += 1

                    intron_emit[char] += 1

        # log it yo
        log.info("total chars seen: {}".format(seen))
        log.info("exon to exon: {}".format(exon_exon))
        log.info("exon to intron: {}".format(exon_intron))
        log.info("exon to end: {}".format(exon_end))

        log.info("intron to intron: {}".format(intron_intron))
        log.info("intron to exon: {}".format(intron_exon))
        
        log.info("Exon emmisions: ")
        log.info("\n\t"+"\n\t".join(["{}:{}".format(x, exon_emit[x]) for x in ["A","T","C","G"]]))
        log.info("Intron emmisions: ")
        log.info("\n\t"+"\n\t".join(["{}:{}".format(x, intron_emit[x]) for x in ["a","t","c","g"]]))

        # calculate transition probabilities
        # begin is done
        
        # Exon
        exon_trans_total = float(exon_exon + exon_intron + exon_end)
        exon_exon = exon_exon / exon_trans_total
        exon_intron = exon_intron / exon_trans_total
        exon_end = exon_end / exon_trans_total

        self.exon.update_edge("exon", exon_exon)
        self.exon.update_edge("intron", exon_intron)
        self.exon.update_edge("end", exon_end)

        intron_trans_total = float(intron_exon + intron_intron)
        intron_exon = intron_exon / intron_trans_total
        intron_intron = intron_intron / intron_trans_total

        self.intron.update_edge("exon", intron_exon)
        self.intron.update_edge("intron", intron_intron)

        # calculate emisson probabilities
        # for each emisson char the prob is counts / total emitted from state (with corrections)
        exon_emitted = sum(exon_emit.values())
        for char in exon_emit:
            prob = exon_emit[char] / float(exon_emitted)
            self.exon.set_emit(char, prob)

        intron_emitted = sum(intron_emit.values())
        for char in intron_emit:
            prob = intron_emit[char] / float(intron_emitted)
            self.intron.set_emit(char, prob)

        log.info("Training done")
        log.info(str(self))

    def log_transform(self):
        self.exon.log_transform()
        self.intron.log_transform()

    def log_viterbi(self, seq):

        # v sub i
        neg_inf = float("-inf")
        vit_vals = { 'start': [], 'exon': [], 'intron':[], 'end':[] }

        # init
        vit_vals['start'].append(self.start.get_edge("exon"))
        vit_vals['exon'].append(neg_inf)
        vit_vals['intron'].append(neg_inf)

        ptr = { 'start':[], 'exon': [], 'intron':[], 'end':[] }

        # recursion for emitting states
        for i in range(1, len(seq)+1):

            # handle start
            vit_vals['start'].append(neg_inf)
            ptr['start'].append('N/A')

            # calculate the exon (l=exon)
            exon_emisson_prob = self.exon.get_emit(seq[i-1].upper()) # this may be off by one, it is unclear

            # from exon we either came from start, exon or intron
            # THIS MAY ALSO BE WRONG, B/C WHAT ABOUT FROM START?
            exon_previous_exon_v = vit_vals['exon'][i-1]
            exon_previous_exon_trans = self.exon.get_edge('exon')
            exon_previous_exon_sum = exon_previous_exon_v + exon_previous_exon_trans

            exon_previous_intron_v = vit_vals['intron'][i-1]
            exon_previous_intron_trans = self.intron.get_edge('exon')
            exon_previous_intron_sum = exon_previous_intron_v + exon_previous_intron_trans

            exon_previous_start_v = vit_vals['start'][i-1]
            exon_previous_start_trans = self.start.get_edge('exon')
            exon_previous_start_sum = exon_previous_start_v + exon_previous_start_trans

            # start with exon
            max_state = "exon"
            max_sum = exon_previous_exon_sum

            if exon_previous_intron_sum >= max_sum:
                max_state = "intron"
                max_sum = exon_previous_intron_sum
            
            if exon_previous_start_sum >= max_sum:
                max_state = "start"
                max_sum = exon_previous_start_sum

            # enter the value
            vit_vals['exon'].append(exon_emisson_prob + max_sum)
            ptr['exon'].append(max_state)

            # TO INTRON NOW
            intron_emit_prob = self.intron.get_emit(seq[i-1])

            intron_previous_exon_v = vit_vals['exon'][i-1]
            intron_previous_exon_trans = self.exon.get_edge('intron')
            intron_previous_exon_sum = intron_previous_exon_v + intron_previous_exon_trans

            intron_previous_intron_v = vit_vals['intron'][i-1]
            intron_previous_intron_trans = self.intron.get_edge('intron')
            intron_previous_intron_sum = intron_previous_intron_v + intron_previous_intron_trans

            max_state = "exon"
            max_sum = intron_previous_exon_sum

            if intron_previous_intron_sum >= max_sum:
                max_state = "intron"
                max_sum = intron_previous_intron_sum

            vit_vals['intron'].append(intron_emit_prob + max_sum)
            ptr['intron'].append(max_state)

            # now end
            # has to be from exon
            vit_vals['end'].append(vit_vals['exon'][i-1] + self.exon.get_edge('end'))
            ptr['end'].append('exon')

        # okay we are done now
        # lets trace back
        # we will build up the result here
        result = []
        state_in = "exon"
        seq = list(seq)
        seq[-1] = seq[-1].upper()

        for i in reversed(range(len(ptr['exon']))):

            # pop from everything
            if state_in == 'exon':
                state_in = ptr['exon'].pop()
                ptr['intron'].pop()
                seq[i] = seq[i].upper()

            elif state_in == 'intron':
                state_in = ptr['intron'].pop()
                ptr['exon'].pop()

        return "".join(seq)

    def viterbi(self, test_seq):

        log.info("%s", test_seq)

        # subproblem: define vk(i) to be the probability of the most
        # probable path accounting for the first i characters of x and
        # ending in state k

        # we want to compute VN(L) , the probability of the most
        # probable path accounting for all of the sequence and
        # ending in the end state 

        # Initilization
        # each state needs a N len array to store probs
        N = len(test_seq)
        neg_inf = float("-inf")

        vit_vals = {"start": [neg_inf] * N, 
             "exon": [neg_inf] * N, 
             "intron": [neg_inf] * N, 
             "end": [neg_inf] * N}

        path_vals = {"start": [None] * N, 
             "exon": [None] * N, 
             "intron": [None] * N, 
             "end": [None] * N}

        # starting values
        vit_vals["start"][0] = 1

        # solve for the rest of the i's
        for i in range(1,N):

            e_prior_exon = vit_vals["exon"][i-1] + self.exon.get_edge("exon")
            e_prior_intron = vit_vals["intron"][i-1] + self.intron.get_edge("exon")
            e_prior_start = vit_vals["start"][i-1] + self.start.get_edge("exon")

            (val, state) = self.compute_vit_exon(test_seq[i-1], e_prior_exon, e_prior_intron, e_prior_start)
            vit_vals["exon"][i] = val
            path_vals["exon"][i] = state

            # intron
            i_prior_exon = vit_vals["exon"][i-1] + self.exon.get_edge("intron")
            i_prior_intron = vit_vals["intron"][i-1] + self.intron.get_edge("intron")

            (val, state) = self.compute_vit_intron(test_seq[i-1], i_prior_exon, i_prior_intron)
            vit_vals["intron"][i] = val
            path_vals["intron"][i] = state

            # for silent states
            # end is all we need to do
            last_prior_exon = vit_vals["exon"][i] + self.exon.get_edge("end")
            vit_vals["end"][i] = last_prior_exon
            path_vals["end"][i] = "exon"

        # run the traceback 
        result = []
        k = N-1
        state_in = path_vals["end"][k]
        result.append(test_seq[k].upper())

        while k > 0:

            if state_in == "exon":
                result.append(test_seq[k-1].upper())

            elif state_in == "intron":
                result.append(test_seq[k-1])

            state_in = path_vals[state_in][k]
            k -= 1

        result.reverse()
        print vit_vals
        return "".join(result)

    def compute_vit_exon(self, char_i, prior_exon, prior_intron, prior_start):
            # exon:
            emit_here_prob = self.exon.get_emit(char_i.upper())

            # we can come from start, exon, or intron
            max_val = prior_exon
            max_state = "exon"

            if max_val < prior_start:
                max_val = prior_start
                max_state = "start"

            if max_val <= prior_intron:
                max_val = prior_intron
                max_state = "intron"

            val =  emit_here_prob + max_val
            return (val, max_state)

    def compute_vit_intron(self, char_i, prior_exon, prior_intron):
            
            # intron:
            emit_here_prob = self.intron.get_emit(char_i)

            # we can come from exon, or intron
            max_val = prior_exon
            max_state = "exon"

            if max_val <= prior_intron:
                max_val = prior_intron
                max_state = "intron"

            val =  emit_here_prob + max_val
            return (val, max_state)

    def __str__(self):
        return "HMM:\n\t"+"\n\n\t".join([str(x) for x in [self.start, self.exon, self.intron, self.end]])

class State(object):

    def __init__(self, label, emissions = None):
        self.label = label
        self.edges = {}
        self.silent = True

        if emissions is not None:
            self.silent = False
            self.emissions = {}

            for x in emissions:
                self.emissions[x] = 0.0

    def __str__(self):

        # load the edges
        string = self.label + "\n\tEDGES: "
        string += " | ".join([ "{}:{}".format(edge, self.edges[edge]) for edge in self.edges])
        string += "\n\tEDGE SUM: {}".format(sum(self.edges.values()))

        if not self.silent:
            string += "\n\tEMISSIONS: "
            string += " | ".join([ "{}:{}".format(emission, self.emissions[emission]) for emission in self.emissions ])
            string += "\n\tEMISSON SUM: {}".format(sum(self.emissions.values()))
        return string

    def log_transform(self):

        for edge in self.edges:
            self.edges[edge] = logrthm(self.edges[edge]) if self.edges[edge] != 0 else float("-inf")

        for emis in self.emissions:
            self.emissions[emis] = logrthm(self.emissions[emis])  if self.emissions[emis] != 0 else float("-inf")

    def add_edge(self, state, prob):
        self.edges[state.label] = prob

    def update_edge(self, label, prob):
        self.edges[label] = prob

    def set_emit(self, char, prob):
        self.emissions[char] = prob

    def get_emit(self, char):
        return self.emissions[char]

    def get_edge(self, label):
        if label in self.edges:
            return self.edges[label]
        else:
            return 0
