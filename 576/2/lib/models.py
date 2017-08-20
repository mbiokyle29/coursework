"""
models.py
author: kgmcchesney

Object models for the overlap aligner
"""
import operator

class OverlapAligner():

    def __init__(self, x_string, y_string, rows, cols, match_score, mis_score, gap_score, space_score):
        
        # bookeeping vals
        self.x_string = x_string
        self.y_string = y_string
        self.neg_inf = float("-inf")
        self.rows = rows
        self.cols = cols
        self.M = "M"
        self.X = "X"
        self.Y = "Y"

        # scores
        self.match_score = match_score
        self.mis_score = mis_score
        self.gap_score = gap_score
        self.space_score = space_score

        # matrix (multi matrix really)
        self.matrix = Matrix(rows, cols)
        self.matrix_keys = [self.M, self.X, self.Y]
        self._init_mtx()

    def _init_mtx(self):

        # do the initilizations
        # M(0,0) = 0
        self.matrix.set(0,0, self.M, 0)
    
        # Ix(i, 0) changed to Ix(i, 0) = 0, for all i
        for i_th in range(0, self.rows):
            
            self.matrix.set(i_th, 0, self.X, 0)
            
            if i_th != 0:
                self.matrix.set(i_th, 0, self.Y, self.neg_inf)
                self.matrix.set(i_th, 0, self.M, self.neg_inf)

        for j_th in range(0, self.cols):
            self.matrix.set(0, j_th, self.Y, (self.gap_score + j_th * self.space_score))

            if j_th != 0:
                self.matrix.set(0, j_th, self.X, self.neg_inf)
                self.matrix.set(0, j_th, self.M, self.neg_inf)

    def dump(self):
        print self.matrix
    
    def dump_pointers(self):
        print self.matrix.pointers()
    
    def align(self):

        # calc everything
        for i in range(1,self.rows):
            for j in range(1, self.cols):
                self.m_reccurence(i,j)
                self.Ix_reccurence(i,j)
                self.Iy_reccurence(i,j)

        # get the largest value
        (max_j, code, score ) = self.get_best_start()
        trace_array = self.traceback((self.rows - 1), max_j, code)

        # build up the 'aligning bits'
        matching = []
        for residue in trace_array:

            code = residue[2]
            
            if code == "M":
                matching.append((self.x_string[residue[0] - 1], self.y_string[residue[1] - 1]))

            elif code == "X":
                matching.append((self.x_string[residue[0] - 1], "-"))

            else:
                matching.append(("-", self.y_string[residue[1] - 1]))


        x_off = trace_array[0][0] - 1
        y_off = trace_array[-1][1]
        top = self.x_string[:x_off] + "".join(match[0] for match in matching)
        bottom = " " * x_off + "".join(match[1] for match in matching) + self.y_string[y_off:]
        print top
        print bottom
        print score

    def traceback(self, i, j, code):

        results = []
        while j != 0:
            
            # add the node
            node = self.matrix.get_node(i,j)
            results.append((i, j, code))

            # calculate the pointer back
            if code == "M":
                back_code = self.matrix.get_back(i,j, self.M)
                i = i - 1
                j = j - 1

            elif code == "X":
                back_code = self.matrix.get_back(i,j, self.X)
                i = i - 1

            else:
                back_code = self.matrix.get_back(i,j, self.Y)
                j = j - 1

            code = back_code

        results.reverse()
        return results

    def get_best_start(self):
        
        # The score of the best overlap alignment can then be obtained by finding the largest value
        # in the last rows of the M, Ix, and Iy matrices, i.e.,
        last_row = self.rows - 1
        
        m_largest = self.neg_inf
        m_idx = None

        x_largest = self.neg_inf
        x_idx = None

        y_largest = self.neg_inf
        y_idx = None

        # tiebreaking: keep largest j within mtx, keep M then X then Y
        # will find the largest of each -- can tie break
        for j in range(0, self.cols):

            m_val = self.matrix.get(last_row, j, self.M)

            if m_val >= m_largest:
                m_largest = m_val
                m_idx = j

            x_val = self.matrix.get(last_row, j, self.X)

            if x_val >= x_largest:
                x_largest = x_val
                x_idx = j

            y_val = self.matrix.get(last_row, j, self.Y)

            if y_val >= y_largest:
                y_largest = y_val
                y_idx = j

        if m_largest >= x_largest and m_largest >= y_largest:
            return (m_idx, self.M, m_largest)

        if x_largest > m_largest and x_largest >= y_largest:
            return (x_idx, self.X, x_largest)
        
        else:
            return (y_idx, self.Y, y_largest)

    def m_reccurence(self, i, j):
        
        # previous indicies
        i_prev = i - 1
        j_prev = j - 1

        ith_char = self.x_string[i-1]
        jth_char = self.y_string[j-1]

        s_val   = self.score(ith_char, jth_char)
        m_prev  = self.matrix.get(i_prev, j_prev, self.M)
        ix_prev = self.matrix.get(i_prev, j_prev, self.X)
        iy_prev = self.matrix.get(i_prev, j_prev, self.Y)
        
        # increment each by the score value  
        m_prev  += s_val
        ix_prev += s_val
        iy_prev += s_val

        score = [m_prev, ix_prev, iy_prev]
        largest_value = max(score)
        backtrack_code = self.matrix_keys[score.index(largest_value)]
        
        self.matrix.set(i, j, self.M, largest_value)
        self.matrix.set_back(i, j, self.M, backtrack_code)

    def Ix_reccurence(self, i, j):

        prev_i = i - 1

        m_prev = self.matrix.get(prev_i, j, self.M)
        m_prev += (self.gap_score + self.space_score)

        ix_prev = self.matrix.get(prev_i, j, self.X)
        ix_prev += self.space_score

        score = [m_prev, ix_prev, self.neg_inf]
        largest_value = max(score)
        backtrack_code = self.matrix_keys[score.index(largest_value)]
        
        self.matrix.set(i, j, self.X, largest_value)
        self.matrix.set_back(i, j, self.X, backtrack_code)
        
    def Iy_reccurence(self, i, j):
        prev_j = j - 1

        m_prev = self.matrix.get(i, prev_j, self.M)
        
        m_prev += (self.gap_score + self.space_score)
        
        iy_prev = self.matrix.get(i, prev_j, self.Y)
        
        iy_prev += self.space_score

        score = [m_prev, self.neg_inf, iy_prev]
        largest_value = max(score)
        backtrack_code = self.matrix_keys[score.index(largest_value)]
        
        self.matrix.set(i, j, self.Y, largest_value)
        self.matrix.set_back(i, j, self.Y, backtrack_code)

    def score(self, a, b):
        return self.match_score if a.upper() == b.upper() else self.mis_score

class Matrix():

    def __init__(self, rows, cols):
        self._mtx = [ [ Cell() for _ in range(0, cols) ] for _ in range(0, rows) ]

    def __str__(self):
        string_rep = ""

        for row in self._mtx:
            string_rep += ",".join([ str(node) for node in row ]) + "\n"

        return string_rep

    def pointers(self):
        string_rep = ""

        for row in self._mtx:
            string_rep += ",".join([ cell.back_pointers() for cell in row ]) + "\n"

        return string_rep 

    def get_node(self, row, col):
        return self._mtx[row][col]
    
    def set(self, row, col, key, val):
        if key == "M":
            return self._mtx[row][col].set_m(val)
        elif key == "X":
            return self._mtx[row][col].set_x(val)
        else:
            return self._mtx[row][col].set_y(val)

    def get(self, row, col, key):
        if key == "M":
            return self._mtx[row][col].get_m()
        elif key == "X":
            return self._mtx[row][col].get_x()
        else:
            return self._mtx[row][col].get_y()

    def set_back(self, row, col, key, val):
        if key == "M":
            return self._mtx[row][col].set_m_back(val)
        elif key == "X":
            return self._mtx[row][col].set_x_back(val)
        else:
            return self._mtx[row][col].set_y_back(val)
    
    def get_back(self, row, col, key):
        if key == "M":
            return self._mtx[row][col].get_m_back()
        elif key == "X":
            return self._mtx[row][col].get_x_back()
        else:
            return self._mtx[row][col].get_y_back()

class Cell():

    def __init__(self):
        self.m = None
        self.m_back = None
        
        self.x = None
        self.x_back = None
        
        self.y = None
        self.y_back = None

    def __str__(self):
        return "M:{}|X:{}|Y:{}".format(str(self.m), str(self.x), str(self.y))

    def back_pointers(self):
        return "M:{}|X:{}|Y:{}".format(str(self.m_back), str(self.x_back), str(self.y_back))

    def set_m(self, val):
        self.m = val

    def set_x(self, val):
        self.x = val

    def set_y(self, val):
        self.y = val

    def get_m(self):
        return self.m

    def get_x(self):
        return self.x

    def get_y(self):
        return self.y

    def set_m_back(self, back):
        self.m_back = back

    def set_x_back(self, back):
        self.x_back = back

    def set_y_back(self, back):
        self.y_back = back

    def get_m_back(self):
        return self.m_back

    def get_x_back(self):
        return self.x_back

    def get_y_back(self):
        return self.y_back