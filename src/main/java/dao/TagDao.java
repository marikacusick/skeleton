package dao;

import generated.tables.Tags;
import generated.tables.records.ReceiptsRecord;
import generated.tables.records.TagsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

import static generated.Tables.RECEIPTS;
import static generated.Tables.TAGS;

public class TagDao {

    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public void tagOrUntag(String tagName, int receiptId){

        TagsRecord tagRecord = dsl.selectFrom(TAGS)
                .where(TAGS.TAG_NAME.eq(tagName))
                .and(TAGS.ID.eq(receiptId))
                .fetchOne();

        if (tagRecord==null){
            // if this tag record does not exist, then we want to tag
            dsl.insertInto(TAGS, TAGS.TAG_NAME, TAGS.ID)
                    .values(tagName, receiptId)
                    .execute();
        }

        else{
            // if this tag record does exist, we want to untag
            dsl.deleteFrom(TAGS)
                    .where(TAGS.TAG_NAME.eq(tagName))
                    .and(TAGS.ID.eq(receiptId))
                    .execute();
        }
    }

    public List<ReceiptsRecord> getListOfTags(String tagName){

        List<ReceiptsRecord> receipts = new ArrayList<>();

        Result<Record1<Integer>> results = dsl.select(TAGS.ID).from(TAGS)
                .where(TAGS.TAG_NAME.eq(tagName))
                .fetch();

        for (Record1 <Integer> result : results){
            ReceiptsRecord receipt = dsl.selectFrom(RECEIPTS).
                    where(RECEIPTS.ID.eq(result.value1()))
                    .fetchOne();
            receipts.add(receipt);
        }

        return receipts;


    }

}
