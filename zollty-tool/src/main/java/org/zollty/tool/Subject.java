/**
 * 
 */
package org.zollty.tool;

/**
 * 
 * @author zollty
 * @since 2018年1月29日
 */
public class Subject {
    
    private String content;
    
    private String option;
    
    private String analysis;
    
    private String comment;
    
    private String answer;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Subject [content=");
        builder.append(content);
        builder.append(", option=");
        builder.append(option);
        builder.append(", analysis=");
        builder.append(analysis);
        builder.append(", comment=");
        builder.append(comment);
        builder.append(", answer=");
        builder.append(answer);
        builder.append("]");
        return builder.toString();
    }
    

}
