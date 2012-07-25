--
-- Created by IntelliJ IDEA.
-- User: carneiro
-- Date: 5/2/12
-- Time: 12:34 PM
--
-- Quick script to combine GATK Reports generated by BQSR from separate read groups.
--
-- Usage:
--
--   lua mergeRecalibrationReports.lua file1.grp file2.grp file3.grp ... fileN.grp > merged.grp

local merged = {{rows = {}}, {rows = {}}, {rows = {}}, {rows = {}}, {rows = {}} }
for order, report in ipairs(arg) do
    local tableIndex = 0                                                        -- the table we are looking at right now
    local skipNextLine = false
    for l in io.lines(report) do
        if l:sub(1,1) == "#"  then                                              -- header processing
            local columns, nrows = l:match("#:GATKTable:.*:(%d+):(%d+):.*")
            if columns ~= nil then
                tableIndex = tableIndex + 1
                merged[tableIndex].columns = columns
                if tableIndex > 2 or (tableIndex <= 2 and order == 1) then
                    merged[tableIndex].nrows = nrows + (merged[tableIndex].nrows or 0)
                end
            elseif l:match("#:GATKTable:.*") then
                merged[tableIndex].title = l
            elseif l:match("#:GATKReport.*") then
                merged.title = l
            else
                print("Something went wrong, cannot recognize line: " .. l)
                os.exit(1)
            end
        elseif l == "" then
            skipNextLine = true
        elseif skipNextLine then
            skipNextLine = false
        else
            if (tableIndex <= 2 and order == 1) or tableIndex > 2 then
                table.insert(merged[tableIndex].rows, l)
            end
        end
    end
end

print(merged.title)
for order, table in ipairs(merged) do
    if     order == 1 then print("#:GATKTable:true:"..table.columns..":"..table.nrows.."::;")
    elseif order == 2 then print("#:GATKTable:true:"..table.columns..":"..table.nrows..":::;")
    elseif order == 3 then print("#:GATKTable:false:"..table.columns..":"..table.nrows..":%s:%s:%.4f:%.4f:%d:%d:;")
    elseif order == 4 then print("#:GATKTable:false:"..table.columns..":"..table.nrows..":%s:%s:%s:%.4f:%d:%d:;")
    elseif order == 5 then print("#:GATKTable:false:"..table.columns..":"..table.nrows..":%s:%s:%s:%s:%s:%.4f:%d:%d:;") end
    print(table.title)

    for _, l in ipairs(table.rows) do print(l) end
    print()
end

